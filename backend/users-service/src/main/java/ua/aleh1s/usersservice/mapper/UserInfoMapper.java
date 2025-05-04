package ua.aleh1s.usersservice.mapper;

import org.mapstruct.Mapper;
import ua.aleh1s.usersservice.domain.UserEntity;
import ua.aleh1s.usersservice.dto.User;

@Mapper(componentModel = "spring")
public interface UserInfoMapper {
    User toUserInfo(UserEntity userEntity);
}
