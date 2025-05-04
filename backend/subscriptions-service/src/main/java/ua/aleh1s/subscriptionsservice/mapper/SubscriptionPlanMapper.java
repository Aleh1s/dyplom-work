package ua.aleh1s.subscriptionsservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ua.aleh1s.subscriptionsservice.domain.SubscriptionPlanEntity;
import ua.aleh1s.subscriptionsservice.dto.SubscriptionPlan;
import ua.aleh1s.subscriptionsservice.utils.MoneyUtils;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface SubscriptionPlanMapper {
    @Mapping(source = "priceInCents", target = "price", qualifiedByName = "centsToDollars")
    SubscriptionPlan toSubscriptionPlan(SubscriptionPlanEntity subscriptionPlanEntity);

    @Named("centsToDollars")
    default BigDecimal convert(Integer priceInCents) {
        return MoneyUtils.centsToDollars(priceInCents);
    }
}
