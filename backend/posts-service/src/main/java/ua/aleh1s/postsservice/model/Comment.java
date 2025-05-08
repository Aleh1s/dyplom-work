package ua.aleh1s.postsservice.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@Builder
@Document(collection = "comments")
public class Comment {
    @Id
    private String id;
    private String userId;
    private String content;
    private String postId;
    private Instant createdAt;
}
