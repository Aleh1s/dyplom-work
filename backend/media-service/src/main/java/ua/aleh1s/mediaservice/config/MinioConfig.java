package ua.aleh1s.mediaservice.config;

import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ua.aleh1s.mediaservice.properties.MinioClientProperties;

@Configuration
@RequiredArgsConstructor
public class MinioConfig {
    private final MinioClientProperties properties;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(properties.getEndpoint())
                .credentials(properties.getAccessKey(), properties.getSecretKey())
                .build();
    }
}
