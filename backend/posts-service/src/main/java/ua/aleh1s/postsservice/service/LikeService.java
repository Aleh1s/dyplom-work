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
import ua.aleh1s.postsservice.exception.ResourcesConflictException;
import ua.aleh1s.postsservice.jwt.ClaimsNames;
import ua.aleh1s.postsservice.model.Like;
import ua.aleh1s.postsservice.repository.LikeRepository;
import ua.aleh1s.postsservice.utils.CommonGenerator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeService {
    private final LikeRepository likeRepository;
    private final MongoTemplate mongoTemplate;
    private final CommonGenerator generator;

    @Transactional
    public void like(String postId) {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        String userId = jwt.getClaimAsString(ClaimsNames.SUBJECT);

        boolean hasUserLike = likeRepository.existsLikeByUserIdAndPostId(userId, postId);
        if (hasUserLike) {
            throw new ResourcesConflictException("Post already liked by user with id: %s".formatted(userId));
        }

        Like like = Like.builder()
                .id(generator.uuid())
                .postId(postId)
                .userId(userId)
                .build();

        likeRepository.save(like);
    }

    @Transactional
    public void unlike(String postId) {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        String userId = jwt.getClaimAsString(ClaimsNames.SUBJECT);

        likeRepository.deleteLikeByUserIdAndPostId(userId, postId);
    }

    public Set<String> getUserLikes(Set<String> postIds) {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        String userId = jwt.getClaimAsString(ClaimsNames.SUBJECT);

        List<Like> likes = likeRepository.getLikesByPostIdInAndUserId(postIds, userId);

        return likes.stream()
                .map(Like::getPostId)
                .collect(Collectors.toSet());
    }

    public Map<String, Long> getLikesCountByPostId(Set<String> postIds) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where(Like.Fields.postId).in(postIds)),
                Aggregation.group(Like.Fields.postId).count().as("count"),
                Aggregation.project("count").and("_id").as(Like.Fields.postId)
        );

        AggregationResults<Document> results = mongoTemplate.aggregate(
                aggregation, Like.COLLECTION_NAME, Document.class
        );

        Map<String, Long> resultMap = new HashMap<>();
        for (Document doc : results.getMappedResults()) {
            String postId = doc.getString(Like.Fields.postId);
            Number count = doc.get("count", Number.class);
            resultMap.put(postId, count.longValue());
        }

        postIds.forEach(postId -> resultMap.putIfAbsent(postId, 0L));

        return resultMap;
    }
}
