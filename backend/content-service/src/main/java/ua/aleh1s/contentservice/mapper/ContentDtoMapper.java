package ua.aleh1s.contentservice.mapper;

import org.mapstruct.Mapper;
import ua.aleh1s.contentservice.dto.ContentDto;
import ua.aleh1s.contentservice.model.Content;

@Mapper(componentModel = "spring")
public interface ContentDtoMapper {
    ContentDto map(Content content);
}
