package ua.aleh1s.mediaservice.service.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ua.aleh1s.mediaservice.domain.Metadata;
import ua.aleh1s.mediaservice.dto.Image;
import ua.aleh1s.mediaservice.dto.SaveMediaArgs;
import ua.aleh1s.mediaservice.dto.SaveMediaResponse;
import ua.aleh1s.mediaservice.exception.ImageProcessorException;
import ua.aleh1s.mediaservice.service.MetadataService;
import ua.aleh1s.mediaservice.service.MinioClientService;
import ua.aleh1s.mediaservice.utils.BlurImageComponent;
import ua.aleh1s.mediaservice.utils.IdDateGeneratorComponent;

import java.io.*;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ImageProcessor implements MediaProcessor {
    private final MinioClientService minioClientService;
    private final IdDateGeneratorComponent generator;
    private final BlurImageComponent blurImage;
    private final MetadataService metadataService;

    private static final String IMAGE_BUCKET = "images";
    private static final String CONTENT_TYPE_PREFIX = "image/";

    @Override
    public SaveMediaResponse apply(MultipartFile image) {
        String fileFormat = image.getContentType().substring(CONTENT_TYPE_PREFIX.length());

        try {
            Image blurredImage = blurImage.blurImage(image.getInputStream(), fileFormat);

            String previewImageName = saveImage(fileFormat, blurredImage.getData(), image.getContentType(), blurredImage.getSize());
            String imageName = saveImage(fileFormat, image.getInputStream(), image.getContentType(), image.getSize());

            return SaveMediaResponse.builder()
                    .mediaUrl(imageName)
                    .previewUrl(previewImageName)
                    .build();
        } catch (IOException e) {
            throw new ImageProcessorException("Cannot process image", e);
        }
    }

    private String saveImage(String fileFormat, InputStream data, String contentType, long size) {
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

    @Override
    public boolean supports(MultipartFile media) {
        String contentType = media.getContentType();

        if (Objects.isNull(contentType)) {
            return false;
        }

        return contentType.startsWith(CONTENT_TYPE_PREFIX);
    }
}
