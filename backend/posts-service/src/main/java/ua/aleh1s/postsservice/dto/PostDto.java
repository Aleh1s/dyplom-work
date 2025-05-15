package ua.aleh1s.postsservice.dto;

import lombok.Builder;
import lombok.Data;
import ua.aleh1s.postsservice.model.PostType;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class PostDto {
    private String id;
    private String description;
    private Instant createdAt;
    private PostType type;
    private UserDto owner;
    private List<ContentDto> content;
}
