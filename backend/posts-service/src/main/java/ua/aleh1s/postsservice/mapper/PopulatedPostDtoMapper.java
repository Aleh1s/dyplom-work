package ua.aleh1s.postsservice.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.aleh1s.postsservice.dto.*;
import ua.aleh1s.postsservice.model.Post;
import ua.aleh1s.postsservice.model.PostType;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PopulatedPostDtoMapper {
    public PopulatedPostDto map(Post post, List<ContentDto> content, UserDto owner, boolean hasUserLike, Long likesCount, List<CommentDto> comments, boolean hasUserSubscription) {
        List<Media> media = content.stream()
                .map(c -> map(c, post.getType(), hasUserSubscription))
                .toList();

        return PopulatedPostDto.builder()
                .id(post.getId())
                .description(post.getDescription())
                .createdAt(post.getCreatedAt())
                .type(post.getType())
                .likesCount(likesCount)
                .commentsCount(comments.size())
                .hasUserLike(hasUserLike)
                .hasUserSubscription(hasUserSubscription)
                .owner(owner)
                .media(media)
                .comments(comments)
                .build();
    }

    private Media map(ContentDto content, PostType postType, boolean hasUserSubscription) {
        if (postType.equals(PostType.FREE) || hasUserSubscription) {
            return Media.builder()
                    .url(content.getMediaUrl())
                    .previewUrl(content.getPreviewUrl())
                    .build();
        }

        return Media.builder()
                .url(null)
                .previewUrl(content.getSafePreviewUrl())
                .build();
    }
}
