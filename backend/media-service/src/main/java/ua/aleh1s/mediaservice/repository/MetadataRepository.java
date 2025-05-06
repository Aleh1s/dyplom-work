package ua.aleh1s.mediaservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ua.aleh1s.mediaservice.domain.Metadata;

import java.util.Optional;

public interface MetadataRepository extends MongoRepository<Metadata, String> {
    Optional<Metadata> findMetadataByBucketNameAndFileName(String bucketName, String fileName);
}
