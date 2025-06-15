package ua.aleh1s.paymentservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class SubscriptionDto {
    private String subscriberId;
    private String subscribedOnId;
    private SubscriptionPlanDto subscriptionPlan;
    private boolean cancelled;
    private Instant createdAt;
    private Instant expiredAt;
}
