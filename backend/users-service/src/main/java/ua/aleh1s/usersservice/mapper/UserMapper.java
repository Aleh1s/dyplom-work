package ua.aleh1s.usersservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.aleh1s.usersservice.domain.UserEntity;
import ua.aleh1s.usersservice.dto.Subscription;
import ua.aleh1s.usersservice.dto.User;
import ua.aleh1s.usersservice.dto.UserProfile;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserEntity userEntity);
    @Mapping(target = "id", source = "user.id")
    UserProfile toUserProfile(UserEntity user, Subscription subscribedOn);
}
