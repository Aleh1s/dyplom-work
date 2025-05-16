package ua.aleh1s.postsservice.dto;

import lombok.Builder;
import lombok.Data;
import ua.aleh1s.postsservice.model.PostType;

import java.time.Instant;

@Data
@Builder
public class PostWithCounts {
    private String id;
    private String description;
    private Instant createdAt;
    private PostType type;
    private long commentsCount;
    private long likesCount;
}
