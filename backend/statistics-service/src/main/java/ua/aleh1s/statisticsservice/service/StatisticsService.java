package ua.aleh1s.statisticsservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ua.aleh1s.statisticsservice.client.PostsApiClient;
import ua.aleh1s.statisticsservice.client.SubscriptionsApiClient;
import ua.aleh1s.statisticsservice.dto.*;

import java.time.Instant;

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

    public UserAnalytics getUserAnalytics(String userId, Instant from, Instant to) {
        SubscriptionAnalytics subscriptionAnalytics = subscriptionsApiClient.getSubscriptionAnalytics(userId, from, to);
        UserPostAnalytics userPostAnalytics = postsApiClient.getAnalytics(userId, from, to);

        return UserAnalytics.builder()
                .subscriberGrowth(subscriptionAnalytics.getSubscriptionsCountByDate())
                .commentsGrowth(userPostAnalytics.getCommentsCountByDate())
                .likesGrowth(userPostAnalytics.getLikesCountByDate())
                .build();
    }
}
