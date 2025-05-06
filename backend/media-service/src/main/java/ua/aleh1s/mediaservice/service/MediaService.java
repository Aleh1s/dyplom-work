package ua.aleh1s.mediaservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ua.aleh1s.mediaservice.domain.Metadata;
import ua.aleh1s.mediaservice.domain.Resource;
import ua.aleh1s.mediaservice.dto.GetMediaArgs;
import ua.aleh1s.mediaservice.dto.SaveMediaResponse;
import ua.aleh1s.mediaservice.exception.MediaTypeNotSupportedException;
import ua.aleh1s.mediaservice.service.handler.MediaProcessor;

import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MediaService {
    private final List<MediaProcessor> processors;
    private final MetadataService metadataService;
    private final MinioClientService minioClientService;

    public SaveMediaResponse processAndSaveMedia(MultipartFile media) {
        for (MediaProcessor processor : processors) {
            if (processor.supports(media)) {
                return processor.apply(media);
            }
        }

        throw new MediaTypeNotSupportedException(
                "Media with type %s is not supported".formatted(media.getContentType())
        );
    }

    public Resource getResource(String bucketName, String fileName) {
        Metadata metadata = metadataService.getMetadataByBucketAndFileName(
            bucketName, fileName
        );

        GetMediaArgs getMediaArgs = GetMediaArgs.builder()
                .bucketName(bucketName)
                .fileName(fileName)
                .build();

        InputStream data = minioClientService.download(getMediaArgs);

        return Resource.builder()
                .data(data)
                .contentType(metadata.getContentType())
                .size(metadata.getSize())
                .build();
    }
}
