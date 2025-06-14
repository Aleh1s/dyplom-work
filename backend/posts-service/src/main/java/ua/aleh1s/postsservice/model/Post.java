package ua.aleh1s.postsservice.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@Builder
@FieldNameConstants
@Document(collection = "posts")
public class Post {
    @Id
    private String id;
    private String description;
    private String ownerId;
    private Set<String> contentIds;
    private PostType type;
    private Instant createdAt;
}
