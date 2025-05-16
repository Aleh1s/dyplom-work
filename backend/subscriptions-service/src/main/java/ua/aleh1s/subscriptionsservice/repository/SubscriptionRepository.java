package ua.aleh1s.subscriptionsservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.aleh1s.subscriptionsservice.domain.SubscriptionEntity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, Long> {
    long countAllBySubscribedOnIdAndCreatedAtBetween(String subscribedOnId, Instant createdAtAfter, Instant createdAtBefore);

    Optional<SubscriptionEntity> findSubscriptionEntityBySubscriberIdAndSubscribedOnIdAndExpiredAtAfter(String subscriberId, String subscribedOnId, Instant expiredAtAfter);

    long countAllBySubscribedOnIdAndExpiredAtAfter(String subscribedOnId, Instant expiredAtAfter);

    List<SubscriptionEntity> findSubscriptionEntitiesBySubscriberIdAndExpiredAtAfter(String subscriberId, Instant expiredAtAfter);

    @Query("select s from SubscriptionEntity s where s.subscribedOnId = :subscribedOnId and extract(year from s.createdAt) = :year ")
    Stream<SubscriptionEntity> findSubscriptionEntitiesBySubscribedOnIdAndCreatedAtYear(@Param("subscribedOnId") String subscribedOnId, @Param("year") int year);
}
