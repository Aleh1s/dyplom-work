package ua.aleh1s.subscriptionsservice.dto;

import lombok.Builder;
import lombok.Data;
import ua.aleh1s.subscriptionsservice.domain.SubscriptionType;

import java.math.BigDecimal;

@Data
@Builder
public class CreateSubscriptionPlan {
    private SubscriptionType type;
    private BigDecimal price;
}
