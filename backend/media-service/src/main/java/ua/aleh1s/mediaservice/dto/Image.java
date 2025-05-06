package ua.aleh1s.mediaservice.dto;

import lombok.Builder;
import lombok.Data;

import java.io.InputStream;

@Data
@Builder
public class Image {
    private InputStream data;
    private long size;
}
