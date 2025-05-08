package ua.aleh1s.contentservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ua.aleh1s.contentservice.model.Content;

public interface ContentRepository extends MongoRepository<Content, String> {
}
