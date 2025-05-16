package ua.aleh1s.subscriptionsservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class SubscriptionAnalytics {
    private List<KeyValue<Instant, Integer>> subscriptionsCountByDate;
}
