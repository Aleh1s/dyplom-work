package ua.aleh1s.postsservice.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class CommentDto {
    private String id;
    private String content;
    private String postId;
    private Instant createdAt;
    private UserDto owner;
}
