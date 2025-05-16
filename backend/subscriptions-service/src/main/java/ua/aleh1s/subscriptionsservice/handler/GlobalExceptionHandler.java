package ua.aleh1s.subscriptionsservice.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ua.aleh1s.subscriptionsservice.exception.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SubscriptionPlanAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleException(SubscriptionPlanAlreadyExistsException e) {
        ApiError apiError = ApiError.builder()
                .description(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(apiError);
    }

    @ExceptionHandler(SubscriptionPlanNotFoundException.class)
    public ResponseEntity<ApiError> handleException(SubscriptionPlanNotFoundException e) {
        ApiError apiError = ApiError.builder()
                .description(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(apiError);

    }

    @ExceptionHandler(SubscriptionNotFoundException.class)
    public ResponseEntity<ApiError> handleException(SubscriptionNotFoundException e) {
        ApiError apiError = ApiError.builder()
                .description(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(apiError);

    }

    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ApiError> handleException(ResourceConflictException e) {
        ApiError apiError = ApiError.builder()
                .description(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(apiError);

    }
}
