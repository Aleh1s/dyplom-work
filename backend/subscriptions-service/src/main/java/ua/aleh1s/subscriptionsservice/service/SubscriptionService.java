package ua.aleh1s.subscriptionsservice.service;

import com.nimbusds.jose.util.Pair;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.aleh1s.subscriptionsservice.domain.SubscriptionEntity;
import ua.aleh1s.subscriptionsservice.domain.SubscriptionPlanEntity;
import ua.aleh1s.subscriptionsservice.domain.SubscriptionType;
import ua.aleh1s.subscriptionsservice.dto.*;
import ua.aleh1s.subscriptionsservice.exception.ResourceConflictException;
import ua.aleh1s.subscriptionsservice.exception.SubscriptionNotFoundException;
import ua.aleh1s.subscriptionsservice.exception.SubscriptionPlanAlreadyExistsException;
import ua.aleh1s.subscriptionsservice.exception.SubscriptionPlanNotFoundException;
import ua.aleh1s.subscriptionsservice.jwt.ClaimsNames;
import ua.aleh1s.subscriptionsservice.mapper.SubscriptionMapper;
import ua.aleh1s.subscriptionsservice.mapper.SubscriptionPlanMapper;
import ua.aleh1s.subscriptionsservice.repository.SubscriptionPlanRepository;
import ua.aleh1s.subscriptionsservice.repository.SubscriptionRepository;
import ua.aleh1s.subscriptionsservice.utils.CommonGenerator;
import ua.aleh1s.subscriptionsservice.utils.MoneyUtils;

import java.math.BigDecimal;
import java.time.*;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubscriptionService {
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanMapper subscriptionPlanMapper;
    private final SubscriptionMapper subscriptionMapper;
    private final CommonGenerator commonGenerator;

    @Transactional
    public SubscriptionPlanEntity createSubscriptionPlan(CreateSubscriptionPlan createSubscriptionPlan) {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String userId = jwt.getClaimAsString(ClaimsNames.SUBJECT);
        boolean subscriptionPlanExists = subscriptionPlanRepository.existsByUserId(
                userId
        );

        if (subscriptionPlanExists) {
            throw new SubscriptionPlanAlreadyExistsException("with user id %s".formatted(userId));
        }

        SubscriptionPlanEntity subscriptionPlan = SubscriptionPlanEntity.builder()
                .userId(userId)
                .type(createSubscriptionPlan.getType())
                .priceInCents(MoneyUtils.dollarsToCents(
                        createSubscriptionPlan.getPrice()
                ))
                .build();

        return subscriptionPlanRepository.save(subscriptionPlan);
    }

    public SubscriptionPlan getSubscriptionPlanByUserId(String userId) {
        return subscriptionPlanMapper.toSubscriptionPlan(
                getSubscriptionPlanEntityByUserId(userId)
        );
    }

    @Transactional
    public SubscriptionPlan getMySubscriptionPlanOrCreateNew() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        String userId = jwt.getClaimAsString(ClaimsNames.SUBJECT);

        SubscriptionPlanEntity subscriptionPlanEntity = subscriptionPlanRepository.findSubscriptionPlanEntityByUserId(userId)
                .orElseGet(() -> {
                    CreateSubscriptionPlan createSubscriptionPlan = CreateSubscriptionPlan.builder()
                            .type(SubscriptionType.FREE)
                            .price(BigDecimal.ZERO)
                            .build();

                    return createSubscriptionPlan(createSubscriptionPlan);
                });

        return subscriptionPlanMapper.toSubscriptionPlan(subscriptionPlanEntity);
    }

    @Transactional
    public SubscriptionPlan updateSubscriptionPlan(UpdateSubscriptionPlan updateSubscriptionPlan) {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        SubscriptionPlanEntity subscriptionPlanEntity = getSubscriptionPlanEntityByUserId(
                jwt.getClaimAsString(ClaimsNames.SUBJECT)
        );

        if (Objects.nonNull(updateSubscriptionPlan.getType())) {
            subscriptionPlanEntity.setType(updateSubscriptionPlan.getType());
        }

        if (Objects.nonNull(updateSubscriptionPlan.getPrice())) {
            subscriptionPlanEntity.setPriceInCents(
                    MoneyUtils.dollarsToCents(updateSubscriptionPlan.getPrice())
            );
        }

        subscriptionPlanEntity = subscriptionPlanRepository.save(subscriptionPlanEntity);

        return subscriptionPlanMapper.toSubscriptionPlan(subscriptionPlanEntity);
    }

    public SubscriptionPlanEntity getSubscriptionPlanEntityByUserId(String userId) {
        return subscriptionPlanRepository.findSubscriptionPlanEntityByUserId(userId)
                .orElseThrow(() -> new SubscriptionPlanNotFoundException("with userId %s".formatted(userId)));
    }

    @Transactional
    public Subscription subscribe(SubscribeRequest subscribeRequest) {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String subscriberId = jwt.getClaimAsString(ClaimsNames.SUBJECT);
        String subscribeOnId = subscribeRequest.getSubscribeOnId();

        Optional<SubscriptionEntity> activeSubscription = findActiveSubscription(subscriberId, subscribeOnId);
        if (activeSubscription.isPresent()) {
            throw new ResourceConflictException("subscription with id %s already exists".formatted(subscribeOnId));
        }

        SubscriptionPlanEntity subscriptionPlanEntity = getSubscriptionPlanEntityByUserId(subscribeOnId);

        // todo: payment process

        Instant createdAt = Instant.now();
        Instant expiredAt = createdAt.atZone(ZoneId.systemDefault())
                .plusMonths(1)
                .toInstant();

        SubscriptionEntity subscriptionEntity = SubscriptionEntity.builder()
                .subscriberId(subscriberId)
                .subscribedOnId(subscribeOnId)
                .createdAt(createdAt)
                .expiredAt(expiredAt)
                .build();

        subscriptionEntity.setSubscriptionPlan(subscriptionPlanEntity);
        subscriptionEntity = subscriptionRepository.save(subscriptionEntity);

        return subscriptionMapper.toSubscription(subscriptionEntity);
    }

    private Optional<SubscriptionEntity> findActiveSubscription(String subscriberId, String subscribedOnId) {
        return subscriptionRepository.findSubscriptionEntityBySubscriberIdAndSubscribedOnIdAndExpiredAtAfter(
                subscriberId, subscribedOnId, commonGenerator.now());
    }

    public Subscription getActiveSubscription(String subscribedOnId) {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String subscriberId = jwt.getClaimAsString(ClaimsNames.SUBJECT);

        Optional<SubscriptionEntity> subscriptionOptional = findActiveSubscription(subscriberId, subscribedOnId);
        if (subscriptionOptional.isEmpty()) {
            throw new SubscriptionNotFoundException("on user with id %s".formatted(subscribedOnId));
        }

        return subscriptionMapper.toSubscription(subscriptionOptional.get());
    }

    public SubscribersInfo getSubscribersInfo(String userId) {
        long totalSubscribers = countActiveSubscriptionsBySubscribedOnId(userId);

        return SubscribersInfo.builder()
                .totalSubscribers(totalSubscribers)
                .build();
    }

    private long countActiveSubscriptionsBySubscribedOnId(String subscribedOnId) {
        return subscriptionRepository.countAllBySubscribedOnIdAndExpiredAtAfter(
                subscribedOnId, commonGenerator.now());
    }

    @Transactional
    public List<SubscriptionEntity> getActiveSubscriptionsBySubscriberId(String subscriberId) {
        return subscriptionRepository.findSubscriptionEntitiesBySubscriberIdAndExpiredAtAfter(
                subscriberId, commonGenerator.now());
    }

    public SubscriptionsStatistics getMonthSubscriptionsStatistics(String userId) {
        long totalActiveSubscriptions = countActiveSubscriptionsBySubscribedOnId(userId);

        ZoneId zoneId = ZoneId.systemDefault();

        LocalDate today = LocalDate.now(zoneId);
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);

        Instant startOfWeekInstant = startOfWeek.atStartOfDay(zoneId).toInstant();
        Instant endOfWeekInstant = endOfWeek.atTime(LocalTime.MAX).atZone(zoneId).toInstant();

        long newSubscriptionsThisWeek = subscriptionRepository.countAllBySubscribedOnIdAndCreatedAtBetween(
            userId, startOfWeekInstant, endOfWeekInstant
        );

        List<KeyValue<Month, Integer>> subscriptionsCountByMonth =
                getSubscriptionsCountByMonthBySubscribedOnId(userId);

        return SubscriptionsStatistics.builder()
                .totalActiveSubscriptions(totalActiveSubscriptions)
                .newThisWeekActiveSubscriptionsCount(newSubscriptionsThisWeek)
                .subscribersCountByMonth(subscriptionsCountByMonth)
                .build();
    }

    private List<KeyValue<Month, Integer>> getSubscriptionsCountByMonthBySubscribedOnId(String subscribedOnId) {
        int currentYear = LocalDate.now().getYear();

        Map<Month, Integer> subscriptionsByMonth = new HashMap<>();
        subscriptionRepository.findSubscriptionEntitiesBySubscribedOnIdAndCreatedAtYear(subscribedOnId, currentYear).forEach(subscription -> {
            Month month = subscription.getCreatedAt()
                    .atZone(ZoneId.systemDefault())
                    .getMonth();

            subscriptionsByMonth.compute(month, (k, v) -> v == null ? 1 : v + 1);
        });

        return subscriptionsByMonth.entrySet().stream()
                .map(entry -> new KeyValue<>(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparingInt(v -> v.key().getValue()))
                .collect(Collectors.toList());
    }

    public SubscriptionAnalytics getSubscriptionAnalytics(String userId, Instant from , Instant to) {
        List<KeyValue<Instant, Integer>> subscriptionsCountByDate = getSubscriptionsCountBySubscribedOnIdAndDateRange(userId, from, to);

        return SubscriptionAnalytics.builder()
                .subscriptionsCountByDate(subscriptionsCountByDate)
                .build();
    }

    private List<KeyValue<Instant, Integer>> getSubscriptionsCountBySubscribedOnIdAndDateRange(String subscribedOnId, Instant from, Instant to) {
        Map<Instant, Integer> subscriptionsCountByDate = new HashMap<>();

        ZoneId zoneId = ZoneId.systemDefault();
        subscriptionRepository.findSubscriptionEntitiesBySubscribedOnIdAndCreatedAtBetween(subscribedOnId, from, to).forEach(subscription -> {
            Instant date = subscription.getCreatedAt()
                    .atZone(zoneId)
                    .toLocalDate()
                    .atStartOfDay(zoneId)
                    .toInstant();

            subscriptionsCountByDate.compute(date, (k, v) -> v == null ? 1 : v + 1);
        });

        return subscriptionsCountByDate.entrySet().stream()
                .map(e -> new KeyValue<>(e.getKey(), e.getValue()))
                .sorted(Comparator.comparing(KeyValue::key))
                .collect(Collectors.toList());
    }
}
