package ua.aleh1s.postsservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ua.aleh1s.postsservice.model.Post;

import java.util.List;

public interface PostRepository extends MongoRepository<Post, String> {
    List<Post> getPostsByOwnerId(String ownerId);
}
