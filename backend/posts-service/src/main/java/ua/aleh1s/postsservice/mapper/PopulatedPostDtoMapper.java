package ua.aleh1s.postsservice.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.aleh1s.postsservice.dto.*;
import ua.aleh1s.postsservice.model.Comment;
import ua.aleh1s.postsservice.model.Post;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PopulatedPostDtoMapper {
    private final CommentDtoMapper commentDtoMapper;

    public PopulatedPostDto map(Post post, List<ContentDto> content, UserDto owner, boolean hasUserLike, Long likesCount, List<CommentDto> comments) {
        List<Media> media = content.stream()
                .map(c -> Media.builder()
                        .url(c.getMediaUrl())
                        .previewUrl(c.getPreviewUrl())
                        .build())
                .toList();

        return PopulatedPostDto.builder()
                .id(post.getId())
                .description(post.getDescription())
                .createdAt(post.getCreatedAt())
                .type(post.getType())
                .likesCount(likesCount)
                .commentsCount(comments.size())
                .hasUserLike(hasUserLike)
                .owner(owner)
                .media(media)
                .comments(comments)
                .build();
    }
}
