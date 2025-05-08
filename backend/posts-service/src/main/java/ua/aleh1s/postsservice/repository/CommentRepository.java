package ua.aleh1s.postsservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ua.aleh1s.postsservice.model.Comment;

public interface CommentRepository extends MongoRepository<Comment, String> {
}
