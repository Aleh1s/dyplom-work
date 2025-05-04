package ua.aleh1s.subscriptionsservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.aleh1s.subscriptionsservice.domain.SubscriptionPlanEntity;

import java.util.Optional;

public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlanEntity, Long> {
    Optional<SubscriptionPlanEntity> findSubscriptionPlanEntityByUserId(String userId);
    boolean existsByUserId(String userId);
}
