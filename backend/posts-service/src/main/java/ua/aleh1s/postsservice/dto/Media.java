package ua.aleh1s.postsservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Media {
    private String url;
    private String previewUrl;
}
