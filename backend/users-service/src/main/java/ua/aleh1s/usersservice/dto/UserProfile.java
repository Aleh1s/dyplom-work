package ua.aleh1s.usersservice.dto;

import lombok.Builder;
import lombok.Data;
import ua.aleh1s.usersservice.domain.SocialMediaType;

import java.util.Map;

@Data
@Builder
public class UserProfile {
    private String username;
    private String email;
    private String givenName;
    private String familyName;
    private String bio;
    private String avatarUrl;
    private Map<SocialMediaType, String> socialMediaLinks;
    private Subscription subscribedOn;
}
