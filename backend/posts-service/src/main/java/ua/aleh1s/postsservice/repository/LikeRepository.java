package ua.aleh1s.postsservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ua.aleh1s.postsservice.model.Like;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public interface LikeRepository extends MongoRepository<Like, String> {
    boolean existsLikeByUserIdAndPostId(String userId, String postId);

    void deleteLikeByUserIdAndPostId(String userId, String postId);

    List<Like> getLikesByPostIdInAndUserId(Collection<String> postIds, String userId);

    long countAllByPostIdInAndCreatedAtBetween(Collection<String> postIds, Instant createdAtAfter, Instant createdAtBefore);

    Stream<Like> findAllByPostIdInAndCreatedAtBetween(Collection<String> postIds, Instant createdAtAfter, Instant createdAtBefore);
}
