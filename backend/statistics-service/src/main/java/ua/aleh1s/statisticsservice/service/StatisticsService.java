package ua.aleh1s.statisticsservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.aleh1s.statisticsservice.client.PaymentsApiClient;
import ua.aleh1s.statisticsservice.client.PostsApiClient;
import ua.aleh1s.statisticsservice.client.SubscriptionsApiClient;
import ua.aleh1s.statisticsservice.dto.PaymentsAnalytics;
import ua.aleh1s.statisticsservice.dto.PaymentsStatistics;
import ua.aleh1s.statisticsservice.dto.PostsStatistics;
import ua.aleh1s.statisticsservice.dto.SubscriptionAnalytics;
import ua.aleh1s.statisticsservice.dto.SubscriptionsStatistics;
import ua.aleh1s.statisticsservice.dto.UserAnalytics;
import ua.aleh1s.statisticsservice.dto.UserOverviewStatistics;
import ua.aleh1s.statisticsservice.dto.UserPostAnalytics;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    private final SubscriptionsApiClient subscriptionsApiClient;
    private final PostsApiClient postsApiClient;
    private final PaymentsApiClient paymentsApiClient;

    public UserOverviewStatistics getUserOverviewStatistics(String userId) {
        SubscriptionsStatistics subscriptionsStatistics = subscriptionsApiClient.getMonthSubscriptionsStatistics(userId);
        PostsStatistics postsStatistics = postsApiClient.getPostsStatistics(userId);
        PaymentsStatistics paymentsStatistics = paymentsApiClient.getPaymentsStatistics(userId);

        return UserOverviewStatistics.builder()
                .totalRevenue(paymentsStatistics.getMonthRevenue())
                .totalRevenueGrowPercent(paymentsStatistics.getMonthlyRevenueGrowthPercentage())
                .totalRevenueByMonth(paymentsStatistics.getMonthlyRevenueStatistics())
                .totalActiveSubscriptionsCount(subscriptionsStatistics.getTotalActiveSubscriptions())
                .newThisWeekActiveSubscriptionsCount(subscriptionsStatistics.getNewThisWeekActiveSubscriptionsCount())
                .publishedFreeContentCount(postsStatistics.getPublishedFreeContentCount())
                .publishedPremiumContentCount(postsStatistics.getPublishedPremiumContentCount())
                .subscribersCountByMonth(subscriptionsStatistics.getSubscribersCountByMonth())
                .engagement(postsStatistics.getEngagement())
                .engagementGrowthPercentage(postsStatistics.getEngagementGrowthPercentage())
                .build();
    }

    public UserAnalytics getUserAnalytics(String userId, Instant from, Instant to) {
        SubscriptionAnalytics subscriptionAnalytics = subscriptionsApiClient.getSubscriptionAnalytics(userId, from, to);
        UserPostAnalytics userPostAnalytics = postsApiClient.getAnalytics(userId, from, to);
        PaymentsAnalytics paymentsAnalytics = paymentsApiClient.getPaymentsAnalytics(userId, from, to);

        return UserAnalytics.builder()
                .revenueGrowth(paymentsAnalytics.getAmountsSumByDate())
                .subscriberGrowth(subscriptionAnalytics.getSubscriptionsCountByDate())
                .commentsGrowth(userPostAnalytics.getCommentsCountByDate())
                .likesGrowth(userPostAnalytics.getLikesCountByDate())
                .build();
    }
}
