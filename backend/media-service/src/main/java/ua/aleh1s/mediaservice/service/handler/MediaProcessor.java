package ua.aleh1s.mediaservice.service.handler;

import org.springframework.web.multipart.MultipartFile;
import ua.aleh1s.mediaservice.dto.SaveSafeMediaResponse;

import java.util.function.Function;

public interface MediaProcessor extends Function<MultipartFile, SaveSafeMediaResponse> {
    boolean supports(MultipartFile media);
}
