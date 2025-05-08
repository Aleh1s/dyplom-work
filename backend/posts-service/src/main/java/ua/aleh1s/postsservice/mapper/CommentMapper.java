package ua.aleh1s.postsservice.mapper;

import org.mapstruct.Mapper;
import ua.aleh1s.postsservice.dto.NewComment;
import ua.aleh1s.postsservice.model.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    Comment map(NewComment newComment);
}
