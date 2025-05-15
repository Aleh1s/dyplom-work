package ua.aleh1s.contentservice.service;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.aleh1s.contentservice.dto.NewContentDto;
import ua.aleh1s.contentservice.dto.SearchContentRequest;
import ua.aleh1s.contentservice.exception.NotFoundException;
import ua.aleh1s.contentservice.jwt.ClaimsNames;
import ua.aleh1s.contentservice.mapper.ContentMapper;
import ua.aleh1s.contentservice.model.Content;
import ua.aleh1s.contentservice.repository.ContentRepository;
import ua.aleh1s.contentservice.utils.CommonGenerator;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContentService {
    private final ContentRepository repository;
    private final ContentMapper mapper;
    private final CommonGenerator gen;
    private final MongoTemplate mongoTemplate;

    @Transactional
    public Content save(NewContentDto newContent) {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        Content content = mapper.map(newContent);
        String ownerId = jwt.getClaimAsString(ClaimsNames.SUBJECT);

        content.setId(gen.uuid());
        content.setCreatedAt(gen.now());
        content.setOwnerId(ownerId);

        return repository.save(content);
    }

    public List<Content> getAllBySearchRequest(SearchContentRequest request) {
        Query query = mapper.createQuery(request);
        return mongoTemplate.find(query, Content.class);
    }

    public Content getContentById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Content with id %s not found".formatted(id)));
    }

    public List<Content> getMyGallery() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        String ownerId = jwt.getClaimAsString(ClaimsNames.SUBJECT);

        return repository.findAllByOwnerId(ownerId);
    }
}
