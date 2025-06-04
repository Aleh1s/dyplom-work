package ua.aleh1s.postsservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class SearchContentRequest {
    private Set<String> ids;
    private String ownerId;
}
