package ua.aleh1s.postsservice.mapper;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import ua.aleh1s.postsservice.dto.GetPostsRequest;
import ua.aleh1s.postsservice.dto.NewPost;
import ua.aleh1s.postsservice.model.Post;

import java.util.Objects;

@Mapper(componentModel = "spring")
public interface PostMapper {
    Post map(NewPost newPost);

    default Query createQuery(GetPostsRequest request) {
        Query query = new Query();

        if (Objects.nonNull(request.getOwnerId()) && !request.getOwnerId().isEmpty()) {
            query.addCriteria(Criteria.where(Post.Fields.ownerId).is(request.getOwnerId()));
        }

        Sort.Direction sortDirection = request.getDirection();
        if (Objects.isNull(sortDirection)) {
            sortDirection = Sort.Direction.DESC;
        }

       query.with(Sort.by(sortDirection, Post.Fields.createdAt));

        return query;
    }
}
