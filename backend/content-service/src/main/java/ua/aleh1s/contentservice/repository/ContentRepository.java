package ua.aleh1s.contentservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ua.aleh1s.contentservice.model.Content;

import java.util.List;

public interface ContentRepository extends MongoRepository<Content, String> {
    List<Content> findAllByOwnerId(String ownerId);
}
