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
import ua.aleh1s.postsservice.client.SubscriptionsApiClient;
import ua.aleh1s.postsservice.client.UsersApiClient;
import ua.aleh1s.postsservice.dto.*;
import ua.aleh1s.postsservice.jwt.ClaimsNames;
import ua.aleh1s.postsservice.mapper.PopulatedPostDtoMapper;
import ua.aleh1s.postsservice.mapper.PostDtoMapper;
import ua.aleh1s.postsservice.mapper.PostMapper;
import ua.aleh1s.postsservice.model.Post;
import ua.aleh1s.postsservice.model.PostType;
import ua.aleh1s.postsservice.repository.PostRepository;
import ua.aleh1s.postsservice.utils.CommonGenerator;
import ua.aleh1s.postsservice.utils.DateRange;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
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
    private final SubscriptionsApiClient subscriptionsApiClient;
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
        Jwt jwt = (Jwt) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        String viewerId = jwt.getClaimAsString(ClaimsNames.SUBJECT);

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

        Map<String, Subscription> subscriptionByOwnerId = subscriptionsApiClient.getSubscriptions(viewerId).stream()
                .collect(Collectors.toMap(Subscription::getSubscribedOnId, Function.identity()));
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
                        commentsByPostId.get(post.getId()),
                        nonNull(subscriptionByOwnerId.get(post.getOwnerId())) || post.getOwnerId().equals(viewerId)
                ))
                .toList();
    }

    @Autowired
    public void setCommentService(@Lazy CommentService commentService) {
        this.commentService = commentService;
    }

    public PostsStatistics getPostsStatisticsByOwnerId(String ownerId) {
        long premiumPostsCount = repository.countPostsByOwnerIdAndType(ownerId, PostType.PREMIUM);
        long freePostsCount = repository.countPostsByOwnerIdAndType(ownerId, PostType.FREE);

        Set<String> postIds = repository.findPostsByOwnerId(ownerId).stream()
                .map(Post::getId)
                .collect(Collectors.toSet());

        DateRange thisMonth = getThisMonthDateRange();
        DateRange lastMonth = getLastMonthDateRange();

        long thisMonthLikesCount = likeService.countLikesByPostIdsAndCreatedAtBetween(postIds, thisMonth.from(), thisMonth.to());
        long thisMonthCommentsCount = commentService.countCommentsByPostIdsAndCreatedAtBetween(postIds, thisMonth.from(), thisMonth.to());

        long lastMonthLikesCount = likeService.countLikesByPostIdsAndCreatedAtBetween(postIds, lastMonth.from(), lastMonth.to());
        long lastMonthCommentsCount = commentService.countCommentsByPostIdsAndCreatedAtBetween(postIds, lastMonth.from(), lastMonth.to());

        long thisMonthEngagement = thisMonthLikesCount + thisMonthCommentsCount;
        long lastMonthEngagement = lastMonthLikesCount + lastMonthCommentsCount;

        BigDecimal engagementGrowthPercentage;
        if (lastMonthEngagement == 0) {
            engagementGrowthPercentage = BigDecimal.ZERO;
        } else {
            engagementGrowthPercentage = BigDecimal.valueOf(thisMonthEngagement - lastMonthEngagement)
                    .divide(BigDecimal.valueOf(lastMonthLikesCount), 2, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(1, RoundingMode.HALF_UP);
        }

        return PostsStatistics.builder()
                .publishedPremiumContentCount(premiumPostsCount)
                .publishedFreeContentCount(freePostsCount)
                .engagement(thisMonthEngagement)
                .engagementGrowthPercentage(engagementGrowthPercentage)
                .build();
    }

    private DateRange getThisMonthDateRange() {
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDate now = LocalDate.now(zoneId);

        Instant startOfMonth = now.withDayOfMonth(1)
                .atStartOfDay(zoneId)
                .toInstant();
        Instant endOfMonth = now.withDayOfMonth(now.lengthOfMonth())
                .atTime(LocalTime.MAX)
                .atZone(zoneId)
                .toInstant();

        return new DateRange(startOfMonth, endOfMonth);
    }

    private DateRange getLastMonthDateRange() {
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDate now = LocalDate.now(zoneId);

        LocalDate prevMonthDate = now.minusMonths(1);

        Instant startOfPrevMonth = prevMonthDate.withDayOfMonth(1)
                .atStartOfDay(zoneId)
                .toInstant();

        Instant endOfPrevMonth = prevMonthDate.withDayOfMonth(prevMonthDate.lengthOfMonth())
                .atTime(LocalTime.MAX)
                .atZone(zoneId)
                .toInstant();

        return new DateRange(startOfPrevMonth, endOfPrevMonth);
    }

    public List<PostWithCounts> getLatestPosts(String ownerId, Integer limit) {
        Pageable pageable = PageRequest.of(
                0, limit, Sort.by("createdAt").descending());

        Page<Post> posts = repository.findPostsByOwnerId(ownerId, pageable);
        Set<String> postsIds = posts.getContent().stream().map(Post::getId).collect(Collectors.toSet());

        Map<String, Long> likesCountByPostId = likeService.getLikesCountByPostId(postsIds);
        Map<String, Long> commentsCountByPostId = commentService.getCommentsCountByPostId(postsIds);

        return posts.map(post -> PostWithCounts.builder()
                    .id(post.getId())
                    .description(post.getDescription())
                    .type(post.getType())
                    .createdAt(post.getCreatedAt())
                    .commentsCount(commentsCountByPostId.get(post.getId()))
                    .likesCount(likesCountByPostId.get(post.getId()))
                    .build())
                .getContent();
    }

    public UserPostAnalytics getAnalytics(String ownerId, Instant from, Instant to) {
        Set<String> postsIds = repository.findPostsByOwnerId(ownerId).stream()
                .map(Post::getId)
                .collect(Collectors.toSet());

        List<KeyValue<Instant, Integer>> likesCountByDate = likeService.getLikesCountByDate(postsIds, from, to);
        List<KeyValue<Instant, Integer>> commentsCountByDate = commentService.getCommentsCountByDate(postsIds, from, to);

        return UserPostAnalytics.builder()
                .likesCountByDate(likesCountByDate)
                .commentsCountByDate(commentsCountByDate)
                .build();
    }
}
