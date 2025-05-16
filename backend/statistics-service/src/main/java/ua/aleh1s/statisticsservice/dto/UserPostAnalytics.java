package ua.aleh1s.statisticsservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class UserPostAnalytics {
    private List<KeyValue<Instant, Integer>> commentsCountByDate;
    private List<KeyValue<Instant, Integer>> likesCountByDate;
}
