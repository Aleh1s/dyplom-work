package ua.aleh1s.mediaservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetMediaArgs {
    private String bucketName;
    private String fileName;
}
