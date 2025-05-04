package ua.aleh1s.subscriptionsservice.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "subscription_plans")
public class SubscriptionPlanEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    @Enumerated(EnumType.STRING)
    private SubscriptionType type;
    private Integer priceInCents;
    private Instant createdAt;
    private Instant updatedAt;
    @Setter(AccessLevel.PRIVATE)
    @OneToMany(mappedBy = "subscriptionPlan", cascade = CascadeType.ALL)
    private List<SubscriptionEntity> subscriptions = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }
}
