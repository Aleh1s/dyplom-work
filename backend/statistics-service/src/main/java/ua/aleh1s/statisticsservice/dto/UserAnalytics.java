package ua.aleh1s.statisticsservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class UserAnalytics {
    private List<KeyValue<Instant, Integer>> subscriberGrowth;
    private List<KeyValue<Instant, Integer>> likesGrowth;
    private List<KeyValue<Instant, Integer>> commentsGrowth;
}
