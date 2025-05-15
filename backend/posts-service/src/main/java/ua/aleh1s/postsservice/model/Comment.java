package ua.aleh1s.postsservice.model;

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
@Document(collection = Comment.COLLECTION_NAME)
public class Comment {
    public static final String COLLECTION_NAME = "comments";

    @Id
    private String id;
    private String userId;
    private String content;
    private String postId;
    private Instant createdAt;
}
