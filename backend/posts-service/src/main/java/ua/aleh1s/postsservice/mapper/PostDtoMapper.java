package ua.aleh1s.postsservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.aleh1s.postsservice.dto.ContentDto;
import ua.aleh1s.postsservice.dto.PostDto;
import ua.aleh1s.postsservice.dto.UserDto;
import ua.aleh1s.postsservice.model.Post;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PostDtoMapper {

    @Mapping(target = "id", source = "post.id")
    @Mapping(target = "description", source = "post.description")
    @Mapping(target = "createdAt", source = "post.createdAt")
    PostDto map(Post post, UserDto owner, List<ContentDto> content);
}
