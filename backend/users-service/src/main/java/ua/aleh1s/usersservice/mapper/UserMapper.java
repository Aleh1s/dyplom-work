package ua.aleh1s.usersservice.mapper;

import org.mapstruct.Mapper;
import ua.aleh1s.usersservice.domain.UserEntity;
import ua.aleh1s.usersservice.dto.Subscription;
import ua.aleh1s.usersservice.dto.User;
import ua.aleh1s.usersservice.dto.UserProfile;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserEntity userEntity);
    UserProfile toUserProfile(UserEntity userEntity, Subscription subscribedOn);
}
