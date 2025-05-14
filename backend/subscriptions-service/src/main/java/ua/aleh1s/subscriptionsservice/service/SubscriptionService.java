package ua.aleh1s.subscriptionsservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.aleh1s.subscriptionsservice.domain.SubscriptionEntity;
import ua.aleh1s.subscriptionsservice.domain.SubscriptionPlanEntity;
import ua.aleh1s.subscriptionsservice.domain.SubscriptionType;
import ua.aleh1s.subscriptionsservice.dto.*;
import ua.aleh1s.subscriptionsservice.exception.SubscriptionNotFoundException;
import ua.aleh1s.subscriptionsservice.exception.SubscriptionPlanAlreadyExistsException;
import ua.aleh1s.subscriptionsservice.exception.SubscriptionPlanNotFoundException;
import ua.aleh1s.subscriptionsservice.jwt.ClaimsNames;
import ua.aleh1s.subscriptionsservice.mapper.SubscriptionMapper;
import ua.aleh1s.subscriptionsservice.mapper.SubscriptionPlanMapper;
import ua.aleh1s.subscriptionsservice.repository.SubscriptionPlanRepository;
import ua.aleh1s.subscriptionsservice.repository.SubscriptionRepository;
import ua.aleh1s.subscriptionsservice.utils.MoneyUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubscriptionService {

    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanMapper subscriptionPlanMapper;
    private final SubscriptionMapper subscriptionMapper;

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

    public Subscription getActiveSubscription(String subscribedOnId) {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String subscriberId = jwt.getClaimAsString(ClaimsNames.SUBJECT);
        Optional<SubscriptionEntity> subscriptionOptional = subscriptionRepository
                .findSubscriptionBySubscriberIdAndSubscribedOnId(subscriberId, subscribedOnId);

        if (subscriptionOptional.isEmpty()) {
            throw new SubscriptionNotFoundException("on user with id %s".formatted(subscribedOnId));
        }

        SubscriptionEntity subscription = subscriptionOptional.get();
        if (subscription.getExpiredAt().isBefore(Instant.now())) {
            subscriptionRepository.delete(subscription);
            throw new SubscriptionNotFoundException("on user with id %s".formatted(subscribedOnId));
        }

        return subscriptionMapper.toSubscription(subscription);
    }
}
