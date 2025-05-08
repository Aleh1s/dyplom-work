package ua.aleh1s.postsservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.aleh1s.postsservice.client.ContentApiClient;
import ua.aleh1s.postsservice.client.UsersApiClient;
import ua.aleh1s.postsservice.dto.*;
import ua.aleh1s.postsservice.jwt.ClaimsNames;
import ua.aleh1s.postsservice.mapper.PostDtoMapper;
import ua.aleh1s.postsservice.mapper.PostMapper;
import ua.aleh1s.postsservice.model.Post;
import ua.aleh1s.postsservice.repository.PostRepository;
import ua.aleh1s.postsservice.utils.CommonGenerator;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    private final MongoTemplate mongoTemplate;

    @Transactional
    public PostDto save(NewPost newPost) {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        String ownerId = jwt.getClaimAsString(ClaimsNames.SUBJECT);

        ContentDto content = contentApiClient.getContentById(newPost.getContentId());
        UserDto user = usersApiClient.getUsers(null, Set.of(ownerId)).getFirst();

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

    public List<PostDto> getPostsByRequest(GetPostsRequest request) {
        List<Post> posts = mongoTemplate.find(postMapper.createQuery(request), Post.class);

        Set<String> contentIds = posts.stream()
                .map(Post::getContentId)
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

        return posts.stream()
                .map(post -> postDtoMapper.map(
                        post,
                        ownerById.get(post.getOwnerId()),
                        contentById.get(post.getContentId())
                ))
                .toList();
    }

//    public Post getById(String id) {
//        return repository.findById(id)
//                .orElseThrow(() -> new NotFoundException("Post not found with id: %s".formatted(id)));
//    }
}
