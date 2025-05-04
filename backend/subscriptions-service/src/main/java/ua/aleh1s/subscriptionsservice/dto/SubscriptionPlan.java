package ua.aleh1s.subscriptionsservice.dto;

import lombok.Builder;
import lombok.Data;
import ua.aleh1s.subscriptionsservice.domain.SubscriptionType;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class SubscriptionPlan {
    private String userId;
    private SubscriptionType type;
    private BigDecimal price;
    private Instant createdAt;
    private Instant updatedAt;
}
