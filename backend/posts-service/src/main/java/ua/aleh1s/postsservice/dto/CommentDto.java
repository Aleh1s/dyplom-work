package ua.aleh1s.postsservice.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class CommentDto {
    private String id;
    private String content;
    private Instant createdAt;
    private UserProfile owner;
}
