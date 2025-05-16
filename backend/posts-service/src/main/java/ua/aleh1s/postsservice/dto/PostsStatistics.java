package ua.aleh1s.postsservice.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PostsStatistics {
    private long publishedPremiumContentCount;
    private long publishedFreeContentCount;
    private long engagement;
    private BigDecimal engagementGrowthPercentage;
}
