package ua.aleh1s.paymentservice.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class SubscriptionPlanDto {
    private String userId;
    private SubscriptionType type;
    private BigDecimal price;
    private Instant createdAt;
    private Instant updatedAt;
}
