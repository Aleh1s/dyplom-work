package ua.aleh1s.mediaservice.service.handler;

import org.springframework.web.multipart.MultipartFile;
import ua.aleh1s.mediaservice.dto.SaveMediaResponse;

import java.util.function.Function;

public interface MediaProcessor extends Function<MultipartFile, SaveMediaResponse> {
    boolean supports(MultipartFile media);
}
