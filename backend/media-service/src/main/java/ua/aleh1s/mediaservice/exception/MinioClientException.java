package ua.aleh1s.mediaservice.exception;

public class MinioClientException extends RuntimeException {
    public MinioClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
