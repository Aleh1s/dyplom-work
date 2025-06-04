package ua.aleh1s.contentservice.model;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@Builder
@FieldNameConstants
@Document(collection = "contents")
public class Content {
    @Id
    private String id;
    private String description;
    private String mediaUrl;
    private String previewUrl;
    private String safePreviewUrl;
    private String ownerId;
    private Instant createdAt;
}
