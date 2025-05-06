package ua.aleh1s.mediaservice.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ua.aleh1s.mediaservice.dto.ApiError;
import ua.aleh1s.mediaservice.exception.ImageProcessorException;
import ua.aleh1s.mediaservice.exception.MinioClientException;
import ua.aleh1s.mediaservice.exception.NotFoundException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MinioClientException.class)
    public ResponseEntity<ApiError> handleException(MinioClientException e) {
        log.error(e.getMessage(), e);

        ApiError apiError = ApiError.builder()
                .description(e.getMessage())
                .build();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(apiError);
    }

    @ExceptionHandler(ImageProcessorException.class)
    public ResponseEntity<ApiError> handleException(ImageProcessorException e) {
        log.error(e.getMessage(), e);

        ApiError apiError = ApiError.builder()
                .description(e.getMessage())
                .build();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(apiError);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleException(NotFoundException e) {
        log.error(e.getMessage(), e);

        ApiError apiError = ApiError.builder()
                .description(e.getMessage())
                .build();

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(apiError);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(Exception e) {
        log.error(e.getMessage(), e);

        ApiError apiError = ApiError.builder()
                .description(e.getMessage())
                .build();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(apiError);
    }
}
