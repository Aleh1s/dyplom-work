package ua.aleh1s.mediaservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ua.aleh1s.mediaservice.domain.Metadata;
import ua.aleh1s.mediaservice.domain.Resource;
import ua.aleh1s.mediaservice.dto.GetMediaArgs;
import ua.aleh1s.mediaservice.dto.MediaUrl;
import ua.aleh1s.mediaservice.dto.SaveMediaArgs;
import ua.aleh1s.mediaservice.dto.SaveSafeMediaResponse;
import ua.aleh1s.mediaservice.exception.ImageProcessorException;
import ua.aleh1s.mediaservice.exception.MediaTypeNotSupportedException;
import ua.aleh1s.mediaservice.service.handler.MediaProcessor;
import ua.aleh1s.mediaservice.utils.IdDateGeneratorComponent;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MediaService {
    private final List<MediaProcessor> processors;
    private final MetadataService metadataService;
    private final MinioClientService minioClientService;
    private final IdDateGeneratorComponent generator;

    private static final String IMAGE_BUCKET = "images";
    private static final String CONTENT_TYPE_PREFIX = "image/";

    @Value("${bff.url}")
    private String bffUrl;

    public SaveSafeMediaResponse processAndSaveMedia(MultipartFile media) {
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

    public MediaUrl upload(MultipartFile media) {
        String contentType = media.getContentType();

        if (Objects.isNull(contentType) || !contentType.startsWith(CONTENT_TYPE_PREFIX)) {
            throw new MediaTypeNotSupportedException("Media type is not supported");
        }

        try {
            String fileFormat = contentType.substring(CONTENT_TYPE_PREFIX.length());

            String fileName = saveImage(fileFormat, media.getInputStream(), contentType, media.getSize());
            String mediaUrl = buildLink(fileName, IMAGE_BUCKET);

            return MediaUrl.builder()
                    .url(mediaUrl)
                    .build();
        } catch (IOException e) {
            throw new ImageProcessorException("Error while processing image", e);
        }
    }

    public String saveImage(String fileFormat, InputStream data, String contentType, long size) {
        String fileName = "%s.%s".formatted(generator.generateId(), fileFormat);

        Metadata metadata = Metadata.builder()
                .fileName(fileName)
                .bucketName(IMAGE_BUCKET)
                .contentType(contentType)
                .size(size)
                .build();

        metadataService.saveMetadata(metadata);

        SaveMediaArgs saveMediaArgs = SaveMediaArgs.builder()
                .bucket(IMAGE_BUCKET)
                .fileName(fileName)
                .data(data)
                .contentType(contentType)
                .size(size)
                .build();

        minioClientService.upload(saveMediaArgs);

        return fileName;
    }

    public String buildLink(String fileName, String bucketName) {
        return "%s/media/%s/%s".formatted(bffUrl, bucketName, fileName);
    }
}
