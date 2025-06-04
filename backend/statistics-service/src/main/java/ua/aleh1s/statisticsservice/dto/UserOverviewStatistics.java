package ua.aleh1s.statisticsservice.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Month;
import java.util.List;

@Data
@Builder
public class UserOverviewStatistics {
    private BigDecimal totalRevenue;
    private BigDecimal totalRevenueGrowPercent;

    private long totalActiveSubscriptionsCount;
    private long newThisWeekActiveSubscriptionsCount;
    private List<KeyValue<Month, Integer>> subscribersCountByMonth;

    private long publishedPremiumContentCount;
    private long publishedFreeContentCount;

    private long engagement;
    private BigDecimal engagementGrowthPercentage;
}
