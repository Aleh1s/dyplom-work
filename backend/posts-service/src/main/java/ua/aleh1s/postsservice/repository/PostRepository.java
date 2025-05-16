package ua.aleh1s.postsservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import ua.aleh1s.postsservice.model.Post;
import ua.aleh1s.postsservice.model.PostType;

import java.util.Set;

public interface PostRepository extends MongoRepository<Post, String> {
    Page<Post> findAllByType(PostType type, Pageable pageable);

    long countPostsByOwnerIdAndType(String ownerId, PostType type);

    Set<Post> findPostsByOwnerId(String ownerId);

    Page<Post> findPostsByOwnerId(String ownerId, Pageable pageable);
}
