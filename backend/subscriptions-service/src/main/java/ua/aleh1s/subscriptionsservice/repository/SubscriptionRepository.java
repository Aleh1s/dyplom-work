package ua.aleh1s.subscriptionsservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.aleh1s.subscriptionsservice.domain.SubscriptionEntity;

import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, Long> {
    Optional<SubscriptionEntity> findSubscriptionBySubscriberIdAndSubscribedOnId(String subscriberId, String subscribedOnId);
}
