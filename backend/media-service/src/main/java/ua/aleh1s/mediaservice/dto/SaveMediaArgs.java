package ua.aleh1s.mediaservice.dto;

import lombok.Builder;
import lombok.Data;

import java.io.InputStream;

@Data
@Builder
public class SaveMediaArgs {
    private String bucket;
    private String fileName;
    private InputStream data;
    private String contentType;
    private Long size;
}
