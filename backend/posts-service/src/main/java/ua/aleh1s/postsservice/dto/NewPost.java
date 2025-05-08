package ua.aleh1s.postsservice.dto;

import lombok.Data;
import ua.aleh1s.postsservice.model.PostType;

@Data
public class NewPost {
    private String description;
    private String contentId;
    private PostType type;
}
