package ua.aleh1s.postsservice.dto;

import lombok.Data;
import ua.aleh1s.postsservice.model.PostType;

import java.time.Instant;

@Data
public class PopulatedPostDto {
    private String id;
    private String description;
    private Instant createdAt;
    private PostType type;
    private int likesCount;
    private boolean liked;
    private UserProfile owner;
    private ContentDto content;
}
