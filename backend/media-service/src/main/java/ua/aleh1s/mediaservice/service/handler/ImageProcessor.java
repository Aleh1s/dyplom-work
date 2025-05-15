package ua.aleh1s.mediaservice.service.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ua.aleh1s.mediaservice.dto.Image;
import ua.aleh1s.mediaservice.dto.SaveSafeMediaResponse;
import ua.aleh1s.mediaservice.exception.ImageProcessorException;
import ua.aleh1s.mediaservice.service.MediaService;
import ua.aleh1s.mediaservice.utils.BlurImageComponent;

import java.io.*;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ImageProcessor implements MediaProcessor {
    private final BlurImageComponent blurImage;
    private MediaService mediaService;

    private static final String IMAGE_BUCKET = "images";
    private static final String CONTENT_TYPE_PREFIX = "image/";

    @Override
    public SaveSafeMediaResponse apply(MultipartFile image) {
        String fileFormat = image.getContentType().substring(CONTENT_TYPE_PREFIX.length());

        try {
            Image blurredImage = blurImage.blurImage(image.getInputStream(), fileFormat);

            String previewImageName = mediaService.saveImage(fileFormat, blurredImage.getData(), image.getContentType(), blurredImage.getSize());
            String imageName = mediaService.saveImage(fileFormat, image.getInputStream(), image.getContentType(), image.getSize());

            String imageUrl = mediaService.buildLink(imageName, IMAGE_BUCKET);
            String safePreviewUrl = mediaService.buildLink(previewImageName, IMAGE_BUCKET);

            return SaveSafeMediaResponse.builder()
                    .mediaUrl(imageUrl)
                    .previewUrl(imageUrl)
                    .safePreviewUrl(safePreviewUrl)
                    .build();
        } catch (IOException e) {
            throw new ImageProcessorException("Cannot process image", e);
        }
    }

    @Override
    public boolean supports(MultipartFile media) {
        String contentType = media.getContentType();

        if (Objects.isNull(contentType)) {
            return false;
        }

        return contentType.startsWith(CONTENT_TYPE_PREFIX);
    }

    @Autowired
    public void setMediaService(@Lazy MediaService mediaService) {
        this.mediaService = mediaService;
    }
}
