package ua.aleh1s.statisticsservice.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
public class PaymentsAnalytics {
    List<KeyValue<Instant, BigDecimal>> amountsSumByDate;
}
