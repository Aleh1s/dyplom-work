package ua.aleh1s.postsservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.aleh1s.postsservice.dto.CommentDto;
import ua.aleh1s.postsservice.dto.UserDto;
import ua.aleh1s.postsservice.model.Comment;

@Mapper(componentModel = "spring")
public interface CommentDtoMapper {
    @Mapping(target = "id", source = "comment.id")
    CommentDto map(Comment comment, UserDto owner);
}
