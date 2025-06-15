package ua.aleh1s.paymentservice.dto;

import lombok.Builder;
import lombok.Data;
import ua.aleh1s.paymentservice.model.KeyValue;

import java.math.BigDecimal;
import java.time.Month;
import java.util.List;

@Data
@Builder
public class PaymentsStatistics {
    private BigDecimal monthRevenue;
    private BigDecimal monthlyRevenueGrowthPercentage;
    private List<KeyValue<Month, BigDecimal>> monthlyRevenueStatistics;
}
