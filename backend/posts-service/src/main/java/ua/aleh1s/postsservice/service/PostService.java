package ua.aleh1s.postsservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.aleh1s.postsservice.client.ContentApiClient;
import ua.aleh1s.postsservice.client.UsersApiClient;
import ua.aleh1s.postsservice.dto.*;
import ua.aleh1s.postsservice.jwt.ClaimsNames;
import ua.aleh1s.postsservice.mapper.PopulatedPostDtoMapper;
import ua.aleh1s.postsservice.mapper.PostDtoMapper;
import ua.aleh1s.postsservice.mapper.PostMapper;
import ua.aleh1s.postsservice.model.Comment;
import ua.aleh1s.postsservice.model.Post;
import ua.aleh1s.postsservice.repository.PostRepository;
import ua.aleh1s.postsservice.utils.CommonGenerator;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository repository;
    private final PostMapper postMapper;
    private final CommonGenerator gen;
    private final UsersApiClient usersApiClient;
    private final ContentApiClient contentApiClient;
    private final PostDtoMapper postDtoMapper;
    private final PopulatedPostDtoMapper populatedPostDtoMapper;
    private final LikeService likeService;
    private final MongoTemplate mongoTemplate;
    private CommentService commentService;

    @Transactional
    public PostDto save(NewPost newPost) {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        String ownerId = jwt.getClaimAsString(ClaimsNames.SUBJECT);

        SearchContentRequest contentSearchRequest = SearchContentRequest.builder()
                .ids(newPost.getContentIds())
                .build();

        UserDto user = usersApiClient.getUsers(null, Set.of(ownerId)).getFirst();
        List<ContentDto> content = contentApiClient.getAllBySearchRequest(contentSearchRequest);

        Post post = postMapper.map(newPost);

        post.setId(gen.uuid());
        post.setCreatedAt(gen.now());
        post.setOwnerId(ownerId);

        repository.save(post);

        return postDtoMapper.map(post, user, content);
    }

    public boolean existsById(String id) {
        return repository.existsById(id);
    }

    public List<PopulatedPostDto> getPostsByRequest(GetPostsRequest request) {
        List<Post> posts = mongoTemplate.find(postMapper.createQuery(request), Post.class);

        return populatePosts(posts);
    }

    public Page<PopulatedPostDto> getPostsFeed(GetPostsFeedRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(),
                Sort.by(Sort.Direction.DESC, Post.Fields.createdAt));

        Page<Post> posts;
        if (nonNull(request.getPostType())) {
            posts = repository.findAllByType(request.getPostType(), pageable);
        } else {
            posts = repository.findAll(pageable);
        }

        List<PopulatedPostDto> populatedPosts = populatePosts(posts.toList());

        return new PageImpl<>(populatedPosts, pageable, posts.getTotalElements());
    }

    private List<PopulatedPostDto> populatePosts(Collection<Post> posts) {
        Set<String> postsIds = posts.stream()
                .map(Post::getId)
                .collect(Collectors.toSet());
        Set<String> contentIds = posts.stream()
                .map(Post::getContentIds)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        Set<String> ownerIds = posts.stream()
                .map(Post::getOwnerId)
                .collect(Collectors.toSet());

        SearchContentRequest contentSearchRequest = SearchContentRequest.builder()
                .ids(contentIds)
                .build();

        Map<String, UserDto> ownerById = usersApiClient.getUsers(null, ownerIds).stream()
                .collect(Collectors.toMap(UserDto::getId, Function.identity()));
        Map<String, ContentDto> contentById = contentApiClient.getAllBySearchRequest(contentSearchRequest).stream()
                .collect(Collectors.toMap(ContentDto::getId, Function.identity()));

        Set<String> userLikes = likeService.getUserLikes(postsIds);
        Map<String, Long> likesCountByPostId = likeService.getLikesCountByPostId(postsIds);
        Map<String, List<CommentDto>> commentsByPostId = commentService.getCommentsByPostId(postsIds);

        return posts.stream()
                .map(post -> populatedPostDtoMapper.map(
                        post,
                        post.getContentIds().stream()
                                .map(contentById::get)
                                .toList(),
                        ownerById.get(post.getOwnerId()),
                        userLikes.contains(post.getId()),
                        likesCountByPostId.get(post.getId()),
                        commentsByPostId.get(post.getId())
                ))
                .toList();
    }

    @Autowired
    public void setCommentService(@Lazy CommentService commentService) {
        this.commentService = commentService;
    }
}
