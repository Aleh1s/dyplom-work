package ua.aleh1s.usersservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.aleh1s.usersservice.domain.UserEntity;
import ua.aleh1s.usersservice.dto.User;
import ua.aleh1s.usersservice.jwt.ClaimsNames;
import ua.aleh1s.usersservice.mapper.UserInfoMapper;
import ua.aleh1s.usersservice.repository.UserRepository;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserInfoMapper userInfoMapper;

    @Transactional
    public User getUserInfoOrCreate() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        UserEntity userEntity = userRepository.findUserEntityByUsername(
                jwt.getClaimAsString(ClaimsNames.USERNAME)
        ).orElseGet(this::buildDefaultUserEntity);

        if (Objects.isNull(userEntity.getId())) {
            userEntity = userRepository.save(userEntity);
        }

        return userInfoMapper.toUserInfo(userEntity);
    }

    private UserEntity buildDefaultUserEntity() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return UserEntity.builder()
                .username(jwt.getClaimAsString(ClaimsNames.USERNAME))
                .email(jwt.getClaimAsString(ClaimsNames.EMAIL))
                .givenName(jwt.getClaimAsString(ClaimsNames.GIVEN_NAME))
                .familyName(jwt.getClaimAsString(ClaimsNames.FAMILY_NAME))
                .build();
    }
}
