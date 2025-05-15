package ua.aleh1s.postsservice.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
@FieldNameConstants
@Document(collection = Like.COLLECTION_NAME)
public class Like {
    public static final String COLLECTION_NAME = "likes";

    @Id
    private String id;
    private String postId;
    private String userId;
}
