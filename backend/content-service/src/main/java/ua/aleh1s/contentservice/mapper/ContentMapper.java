package ua.aleh1s.contentservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import ua.aleh1s.contentservice.dto.NewContentDto;
import ua.aleh1s.contentservice.dto.SearchContentRequest;
import ua.aleh1s.contentservice.model.Content;

import java.util.Objects;

@Mapper(componentModel = "spring")
public interface ContentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Content map(NewContentDto newContentDto);

    default Query createQuery(SearchContentRequest request) {
        Query query = new Query();

        if (Objects.nonNull(request.getIds()) && !request.getIds().isEmpty()) {
           query.addCriteria(Criteria.where(Content.Fields.id).in(request.getIds()));
        }

        if (Objects.nonNull(request.getOwnerId()) && !request.getOwnerId().isBlank()) {
            query.addCriteria(Criteria.where(Content.Fields.ownerId).is(request.getOwnerId()));
        }

        return query;
    }
}
