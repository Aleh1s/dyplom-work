package ua.aleh1s.postsservice.dto;

import lombok.Data;
import org.springframework.data.domain.Sort;

@Data
public class GetPostsRequest {
    private String ownerId;
    private Sort.Direction direction;
}
