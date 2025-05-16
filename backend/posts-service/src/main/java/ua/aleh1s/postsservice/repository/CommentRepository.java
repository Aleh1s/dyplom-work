package ua.aleh1s.postsservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ua.aleh1s.postsservice.model.Comment;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public interface CommentRepository extends MongoRepository<Comment, String> {
    List<Comment> findCommentsByPostIdIn(Collection<String> postIds);

    long countCommentsByPostIdInAndCreatedAtBetween(Collection<String> postIds, Instant createdAtAfter, Instant createdAtBefore);

    Stream<Comment> findAllByPostIdInAndCreatedAtBetween(Collection<String> postIds, Instant createdAtAfter, Instant createdAtBefore);
}
