package ua.aleh1s.postsservice.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class ContentDto {
    private String id;
    private String description;
    private String mediaUrl;
    private String previewUrl;
    private String safePreviewUrl;
    private String ownerId;
    private Instant createdAt;
}
