package ua.aleh1s.statisticsservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.aleh1s.statisticsservice.client.PostsApiClient;
import ua.aleh1s.statisticsservice.client.SubscriptionsApiClient;
import ua.aleh1s.statisticsservice.dto.UserOverviewStatistics;
import ua.aleh1s.statisticsservice.dto.PostsStatistics;
import ua.aleh1s.statisticsservice.dto.SubscriptionsStatistics;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    private final SubscriptionsApiClient subscriptionsApiClient;
    private final PostsApiClient postsApiClient;

    public UserOverviewStatistics getUserOverviewStatistics(String userId) {
        SubscriptionsStatistics subscriptionsStatistics = subscriptionsApiClient.getMonthSubscriptionsStatistics(userId);
        PostsStatistics postsStatistics = postsApiClient.getPostsStatistics(userId);

        return UserOverviewStatistics.builder()
                .totalActiveSubscriptionsCount(subscriptionsStatistics.getTotalActiveSubscriptions())
                .newThisWeekActiveSubscriptionsCount(subscriptionsStatistics.getNewThisWeekActiveSubscriptionsCount())
                .publishedFreeContentCount(postsStatistics.getPublishedFreeContentCount())
                .publishedPremiumContentCount(postsStatistics.getPublishedPremiumContentCount())
                .subscribersCountByMonth(subscriptionsStatistics.getSubscribersCountByMonth())
                .engagement(postsStatistics.getEngagement())
                .engagementGrowthPercentage(postsStatistics.getEngagementGrowthPercentage())
                .build();
    }
}
