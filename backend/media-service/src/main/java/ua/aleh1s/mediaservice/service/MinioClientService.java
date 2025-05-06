package ua.aleh1s.mediaservice.service;

import io.minio.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.aleh1s.mediaservice.dto.GetMediaArgs;
import ua.aleh1s.mediaservice.dto.SaveMediaArgs;
import ua.aleh1s.mediaservice.exception.MinioClientException;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class MinioClientService {
    private final MinioClient minioClient;

    public void upload(SaveMediaArgs saveMediaArgs) {
        try {
            BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder()
                    .bucket(saveMediaArgs.getBucket())
                    .build();

            if (!minioClient.bucketExists(bucketExistsArgs)) {
                MakeBucketArgs makeBucketArgs = MakeBucketArgs.builder()
                        .bucket(saveMediaArgs.getBucket())
                        .build();

                minioClient.makeBucket(makeBucketArgs);
            }

            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                .bucket(saveMediaArgs.getBucket())
                .object(saveMediaArgs.getFileName())
                .stream(saveMediaArgs.getData(), saveMediaArgs.getSize(), -1)
                .contentType(saveMediaArgs.getContentType())
                .build();

            minioClient.putObject(putObjectArgs);
        } catch (Exception e) {
            throw new MinioClientException("Cannot upload media", e);
        }
    }

    public InputStream download(GetMediaArgs getMediaArgs) {
        GetObjectArgs args = GetObjectArgs.builder()
                .bucket(getMediaArgs.getBucketName())
                .object(getMediaArgs.getFileName())
                .build();

        try {
            return minioClient.getObject(args);
        } catch (Exception e) {
            throw new MinioClientException("Cannot download media", e);
        }
    }
}
