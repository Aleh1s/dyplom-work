package ua.aleh1s.subscriptionsservice.mapper;

import org.mapstruct.Mapper;
import ua.aleh1s.subscriptionsservice.domain.SubscriptionEntity;
import ua.aleh1s.subscriptionsservice.dto.Subscription;

@Mapper(componentModel = "spring", uses = { SubscriptionPlanMapper.class })
public interface SubscriptionMapper {
    Subscription toSubscription(SubscriptionEntity subscriptionEntity);
}
