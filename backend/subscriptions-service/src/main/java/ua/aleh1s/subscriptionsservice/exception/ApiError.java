package ua.aleh1s.subscriptionsservice.exception;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiError {
    private String description;
}
