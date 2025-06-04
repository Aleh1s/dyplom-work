package ua.aleh1s.postsservice.service;

import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.aleh1s.postsservice.client.UsersApiClient;
import ua.aleh1s.postsservice.dto.*;
import ua.aleh1s.postsservice.exception.NotFoundException;
import ua.aleh1s.postsservice.jwt.ClaimsNames;
import ua.aleh1s.postsservice.mapper.CommentDtoMapper;
import ua.aleh1s.postsservice.mapper.CommentMapper;
import ua.aleh1s.postsservice.model.Comment;
import ua.aleh1s.postsservice.repository.CommentRepository;
import ua.aleh1s.postsservice.utils.CommonGenerator;

import java.time.Instant;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private static final String COUNT_FIELD = "count";

    private final CommentMapper commentMapper;
    private final CommentDtoMapper commentDtoMapper;
    private final CommonGenerator gen;
    private final UsersApiClient usersApiClient;
    private final CommentRepository commentRepository;
    private final PostService postService;
    private final MongoTemplate mongoTemplate;

    @Transactional
    public CommentDto save(String postId, NewComment newComment) {
        boolean postExistsById = postService.existsById(postId);
        if (!postExistsById) {
            throw new NotFoundException("Post not found with id: %s".formatted(postId));
        }

        Jwt jwt = (Jwt) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        String ownerId = jwt.getClaimAsString(ClaimsNames.SUBJECT);
        UserDto owner = usersApiClient.getUsers(null, Set.of(ownerId)).getFirst();

        Comment comment = commentMapper.map(newComment);
        comment.setId(gen.uuid());
        comment.setUserId(owner.getId());
        comment.setCreatedAt(gen.now());
        comment.setPostId(postId);

        commentRepository.save(comment);

        return commentDtoMapper.map(comment, owner);
    }

    public Map<String, List<CommentDto>> getCommentsByPostId(Set<String> postIds) {
        List<Comment> comments = commentRepository.findCommentsByPostIdIn(postIds);

        Set<String> userIds = comments.stream().map(Comment::getUserId).collect(Collectors.toSet());
        Map<String, UserDto> ownerById = usersApiClient.getUsers(null, userIds).stream()
                .collect(Collectors.toMap(UserDto::getId, Function.identity()));

        Map<String, List<CommentDto>> commentsByPostId = comments.stream()
                .map(comment -> commentDtoMapper.map(comment, ownerById.get(comment.getUserId())))
                .collect(Collectors.groupingBy(CommentDto::getPostId));

        postIds.forEach(postId -> commentsByPostId.putIfAbsent(postId, new ArrayList<>()));

        return commentsByPostId;
    }

    public long countCommentsByPostIdsAndCreatedAtBetween(Set<String> postIds, Instant from, Instant to) {
        return commentRepository.countCommentsByPostIdInAndCreatedAtBetween(postIds, from, to);
    }

    public Map<String, Long> getCommentsCountByPostId(Set<String> postsIds) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where(Comment.Fields.postId).in(postsIds)),
                Aggregation.group(Comment.Fields.postId).count().as(COUNT_FIELD),
                Aggregation.project(COUNT_FIELD).and("_id").as(Comment.Fields.postId)
        );

        AggregationResults<Document> results = mongoTemplate.aggregate(
                aggregation, Comment.COLLECTION_NAME, Document.class
        );

        Map<String, Long> resultMap = new HashMap<>();
        for (Document doc : results.getMappedResults()) {
            String postId = doc.getString(Comment.Fields.postId);
            Number count = doc.get(COUNT_FIELD, Number.class);
            resultMap.put(postId, count.longValue());
        }

        postsIds.forEach(postId -> resultMap.putIfAbsent(postId, 0L));

        return resultMap;
    }

    public List<KeyValue<Instant, Integer>> getCommentsCountByDate(Set<String> postIds, Instant from, Instant to) {
        Map<Instant, Integer> commentsCountByDate = new HashMap<>();

        ZoneId zoneId = ZoneId.systemDefault();
        commentRepository.findAllByPostIdInAndCreatedAtBetween(postIds, from, to).forEach(comment -> {
            Instant date = comment.getCreatedAt()
                    .atZone(zoneId)
                    .toLocalDate()
                    .atStartOfDay(zoneId)
                    .toInstant();

            commentsCountByDate.compute(date, (k, v) -> v == null ? 1 : v + 1);
        });

        return commentsCountByDate.entrySet().stream()
                .map(e -> new KeyValue<>(e.getKey(), e.getValue()))
                .sorted(Comparator.comparing(KeyValue::key))
                .toList();
    }
}
