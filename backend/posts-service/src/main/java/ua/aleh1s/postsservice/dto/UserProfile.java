package ua.aleh1s.postsservice.dto;

import lombok.Data;

@Data
public class UserProfile {
    private String id;
    private String email;
    private String username;
    private String givenName;
    private String familyName;
    private String bio;
    private String avatarUrl;
}
