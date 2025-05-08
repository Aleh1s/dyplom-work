package ua.aleh1s.postsservice.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "likes")
public class Like {
    private String postId;
    private String userId;
}
