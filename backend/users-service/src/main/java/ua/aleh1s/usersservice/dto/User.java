package ua.aleh1s.usersservice.dto;

import lombok.*;
import ua.aleh1s.usersservice.domain.SocialMediaType;

import java.util.Map;

@Data
@Builder
public class User {
    private Long id;
    private String username;
    private String email;
    private String bio;
    private String avatarUrl;
    private Map<SocialMediaType, String> socialMediaLinks;
}
