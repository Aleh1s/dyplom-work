package ua.aleh1s.paymentservice.handler;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ua.aleh1s.paymentservice.exceptions.ApiError;
import ua.aleh1s.paymentservice.exceptions.PaymentServiceException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PaymentServiceException.class)
    public ResponseEntity<ApiError> handleException(PaymentServiceException e) {
        ApiError apiError = ApiError.builder()
                .description(e.getMessage())
                .build();

        return ResponseEntity.badRequest()
                .body(apiError);
    }

    @ExceptionHandler(FeignException.Unauthorized.class)
    public ResponseEntity<ApiError> handleException(FeignException.Unauthorized e) {
        ApiError apiError = ApiError.builder()
                .description(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(apiError);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(Exception e) {
        log.error(e.getMessage(), e);

        ApiError apiError = ApiError.builder()
                .description("Something went wrong")
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(apiError);
    }
}
