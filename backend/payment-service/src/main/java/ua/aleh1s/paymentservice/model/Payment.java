package ua.aleh1s.paymentservice.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@Builder
@FieldNameConstants
@Document(collection = "payments")
public class Payment {
    public static final String COLLECTION_NAME = "payments";

    @Id
    private String id;
    private Long amount;
    private String sessionId;
    private String subscriberId;
    private String subscribedOnId;
    private Instant createdAt;
}
