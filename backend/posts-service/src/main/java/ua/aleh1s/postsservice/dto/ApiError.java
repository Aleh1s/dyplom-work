package ua.aleh1s.postsservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiError {
    private String description;
}
