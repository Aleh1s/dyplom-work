package ua.aleh1s.subscriptionsservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Month;
import java.util.List;

@Data
@Builder
public class SubscriptionsStatistics {
    private long totalActiveSubscriptions;
    private long newThisWeekActiveSubscriptionsCount;
    private List<KeyValue<Month, Integer>> subscribersCountByMonth;
}
