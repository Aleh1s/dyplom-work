package ua.aleh1s.mediaservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveSafeMediaResponse {
    private String mediaUrl;
    private String previewUrl;
    private String safePreviewUrl;
}
