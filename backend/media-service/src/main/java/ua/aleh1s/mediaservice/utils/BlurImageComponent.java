package ua.aleh1s.mediaservice.utils;

import org.springframework.stereotype.Component;
import ua.aleh1s.mediaservice.dto.Image;
import ua.aleh1s.mediaservice.exception.MediaTypeNotSupportedException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.*;
import java.util.Arrays;
import java.util.Objects;

@Component
public class BlurImageComponent {
    private static final Integer IMAGE_BLUR_RADIUS = 11;

    public Image blurImage(InputStream input, String format) throws IOException {
        BufferedImage image = ImageIO.read(input);

        if (Objects.isNull(image)) {
            throw new MediaTypeNotSupportedException("This media type is not supported");
        }

        int size = IMAGE_BLUR_RADIUS * 2 + 1;
        float weight = 1.0f / (size * size);
        float[] kernelData = new float[size * size];

        Arrays.fill(kernelData, weight);

        Kernel kernel = new Kernel(size, size, kernelData);
        ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_ZERO_FILL, null);
        BufferedImage blurredImage = op.filter(image, null);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(blurredImage, format, os);
        ByteArrayInputStream data = new ByteArrayInputStream(os.toByteArray());

        return Image.builder()
                .data(data)
                .size(os.size())
                .build();
    }
}
