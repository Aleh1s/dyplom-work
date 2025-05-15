package ua.aleh1s.postsservice.dto;

import lombok.Data;
import ua.aleh1s.postsservice.model.PostType;

@Data
public class GetPostsFeedRequest {
    private int page;
    private int size;
    private PostType postType;
}
