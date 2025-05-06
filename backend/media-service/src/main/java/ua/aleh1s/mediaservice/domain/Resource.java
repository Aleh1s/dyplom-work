package ua.aleh1s.mediaservice.domain;

import lombok.Builder;
import lombok.Data;

import java.io.InputStream;

@Data
@Builder
public class Resource {
    private InputStream data;
    private String contentType;
    private Long size;
}
