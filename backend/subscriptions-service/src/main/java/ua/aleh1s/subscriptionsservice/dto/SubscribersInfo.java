package ua.aleh1s.subscriptionsservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubscribersInfo {
    private long totalSubscribers;
}
