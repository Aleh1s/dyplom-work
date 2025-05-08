package ua.aleh1s.contentservice.dto;

import lombok.Data;

@Data
public class NewContentDto {
    private String description;
    private String mediaUrl;
    private String previewUrl;
    private String safePreviewUrl;
}
