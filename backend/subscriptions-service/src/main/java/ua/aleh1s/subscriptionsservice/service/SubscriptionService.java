package ua.aleh1s.subscriptionsservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.aleh1s.subscriptionsservice.domain.SubscriptionPlanEntity;
import ua.aleh1s.subscriptionsservice.dto.CreateSubscriptionPlan;
import ua.aleh1s.subscriptionsservice.dto.SubscriptionPlan;
import ua.aleh1s.subscriptionsservice.dto.UpdateSubscriptionPlan;
import ua.aleh1s.subscriptionsservice.exception.SubscriptionPlanAlreadyExistsException;
import ua.aleh1s.subscriptionsservice.exception.SubscriptionPlanNotFoundException;
import ua.aleh1s.subscriptionsservice.jwt.ClaimsNames;
import ua.aleh1s.subscriptionsservice.mapper.SubscriptionPlanMapper;
import ua.aleh1s.subscriptionsservice.repository.SubscriptionPlanRepository;
import ua.aleh1s.subscriptionsservice.utils.MoneyUtils;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubscriptionService {

    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final SubscriptionPlanMapper subscriptionPlanMapper;

    @Transactional
    public SubscriptionPlan createSubscriptionPlan(CreateSubscriptionPlan createSubscriptionPlan) {
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

        subscriptionPlan = subscriptionPlanRepository.save(subscriptionPlan);

        return subscriptionPlanMapper.toSubscriptionPlan(subscriptionPlan);
    }

    public SubscriptionPlan getSubscriptionPlanByUserId(String userId) {
        return subscriptionPlanMapper.toSubscriptionPlan(
                getSubscriptionPlanEntityByUserId(userId)
        );
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
}
