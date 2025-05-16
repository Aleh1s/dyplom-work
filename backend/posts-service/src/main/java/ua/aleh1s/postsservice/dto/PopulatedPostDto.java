package ua.aleh1s.postsservice.dto;

import lombok.Builder;
import lombok.Data;
import ua.aleh1s.postsservice.model.PostType;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class PopulatedPostDto {
    private String id;
    private String description;
    private Instant createdAt;
    private PostType type;
    private long likesCount;
    private long commentsCount;
    private boolean hasUserLike;
    private boolean hasUserSubscription;
    private UserDto owner;
    private List<Media> media;
    private List<CommentDto> comments;
}
