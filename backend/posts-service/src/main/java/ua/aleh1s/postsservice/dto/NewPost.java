package ua.aleh1s.postsservice.dto;

import lombok.Data;
import ua.aleh1s.postsservice.model.PostType;

import java.util.Set;

@Data
public class NewPost {
    private String description;
    private Set<String> contentIds;
    private PostType type;
}
