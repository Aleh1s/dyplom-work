package ua.aleh1s.usersservice.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Entity
@Getter
@Setter
@Builder
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    private String id;
    private String givenName;
    private String familyName;
    private String username;
    private String email;
    private String bio;
    private String avatarUrl;

    @ElementCollection
    @CollectionTable(
            name = "user_social_links",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @MapKeyColumn(name = "social_media_type")
    @MapKeyEnumerated(EnumType.STRING)
    @Column(name = "link")
    private Map<SocialMediaType, String> socialMediaLinks = new HashMap<>();
}
