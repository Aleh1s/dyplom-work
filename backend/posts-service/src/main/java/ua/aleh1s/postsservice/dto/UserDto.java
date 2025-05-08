package ua.aleh1s.postsservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    private String id;
    private String username;
    private String email;
    private String givenName;
    private String familyName;
    private String bio;
    private String avatarUrl;
}
