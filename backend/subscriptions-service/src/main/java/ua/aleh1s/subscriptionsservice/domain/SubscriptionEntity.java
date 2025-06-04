package ua.aleh1s.subscriptionsservice.domain;


import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "subscriptions")
public class SubscriptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String subscriberId;
    private String subscribedOnId;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_plan_id")
    private SubscriptionPlanEntity subscriptionPlan;
    private boolean isCancelled;
    private Instant createdAt;
    private Instant expiredAt;

    public void setSubscriptionPlan(SubscriptionPlanEntity subscriptionPlan) {
        this.subscriptionPlan = subscriptionPlan;
        subscriptionPlan.getSubscriptions().add(this);
    }
}
