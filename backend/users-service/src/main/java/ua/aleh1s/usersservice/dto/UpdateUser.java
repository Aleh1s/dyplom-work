package ua.aleh1s.usersservice.dto;

import lombok.Builder;
import lombok.Data;
import ua.aleh1s.usersservice.domain.SocialMediaType;

import java.util.Map;

@Data
@Builder
public class UpdateUser {
    private String givenName;
    private String familyName;
    private String bio;
    private String avatarUrl;
    private Map<SocialMediaType, String> socialMediaLinks;
}
