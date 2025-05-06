package ua.aleh1s.mediaservice.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
@Document(collection = "metadata")
public class Metadata {
    @Id
    private ObjectId id;
    private String fileName;
    private String bucketName;
    private String contentType;
    private Long size;
}
