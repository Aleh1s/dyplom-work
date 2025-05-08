package ua.aleh1s.postsservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.aleh1s.postsservice.client.UsersApiClient;
import ua.aleh1s.postsservice.dto.CommentDto;
import ua.aleh1s.postsservice.dto.NewComment;
import ua.aleh1s.postsservice.dto.UserProfile;
import ua.aleh1s.postsservice.exception.NotFoundException;
import ua.aleh1s.postsservice.jwt.ClaimsNames;
import ua.aleh1s.postsservice.mapper.CommentDtoMapper;
import ua.aleh1s.postsservice.mapper.CommentMapper;
import ua.aleh1s.postsservice.model.Comment;
import ua.aleh1s.postsservice.repository.CommentRepository;
import ua.aleh1s.postsservice.utils.CommonGenerator;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final CommentMapper commentMapper;
    private final CommentDtoMapper commentDtoMapper;
    private final CommonGenerator gen;
    private final UsersApiClient usersApiClient;
    private final CommentRepository commentRepository;
    private final PostService postService;

    @Transactional
    public CommentDto save(String postId, NewComment newComment) {
        boolean postExistsById = postService.existsById(postId);
        if (!postExistsById) {
            throw new NotFoundException("Post not found with id: %s".formatted(postId));
        }

        Jwt jwt = (Jwt) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        String username = jwt.getClaimAsString(ClaimsNames.USERNAME);
        UserProfile owner = usersApiClient.getUserProfileByUsername(username);

        Comment comment = commentMapper.map(newComment);
        comment.setId(gen.uuid());
        comment.setUserId(owner.getId());
        comment.setCreatedAt(gen.now());
        comment.setPostId(postId);

        commentRepository.save(comment);

        return commentDtoMapper.map(comment, owner);
    }
}
