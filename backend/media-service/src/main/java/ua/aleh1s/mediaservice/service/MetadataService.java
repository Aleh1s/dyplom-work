package ua.aleh1s.mediaservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.aleh1s.mediaservice.domain.Metadata;
import ua.aleh1s.mediaservice.exception.NotFoundException;
import ua.aleh1s.mediaservice.repository.MetadataRepository;

@Service
@RequiredArgsConstructor
public class MetadataService {
    private final MetadataRepository repository;

    public void saveMetadata(Metadata metadata) {
        repository.save(metadata);
    }

    public Metadata getMetadataByBucketAndFileName(String bucketName, String fileName) {
        return repository.findMetadataByBucketNameAndFileName(bucketName, fileName)
                .orElseThrow(() -> new NotFoundException(
                        "Metadata with file name %s not found".formatted(fileName))
                );
    }
}
