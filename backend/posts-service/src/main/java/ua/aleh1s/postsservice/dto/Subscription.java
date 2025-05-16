package ua.aleh1s.postsservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class Subscription {
    private String subscriberId;
    private String subscribedOnId;
    private SubscriptionPlan subscriptionPlan;
    private Instant createdAt;
    private Instant expiredAt;
}
