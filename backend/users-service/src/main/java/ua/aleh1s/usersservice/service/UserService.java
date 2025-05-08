package ua.aleh1s.usersservice.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.aleh1s.usersservice.client.SubscriptionsClient;
import ua.aleh1s.usersservice.domain.UserEntity;
import ua.aleh1s.usersservice.dto.Subscription;
import ua.aleh1s.usersservice.dto.UpdateUser;
import ua.aleh1s.usersservice.dto.User;
import ua.aleh1s.usersservice.dto.UserProfile;
import ua.aleh1s.usersservice.exception.UserNotFoundException;
import ua.aleh1s.usersservice.jwt.ClaimsNames;
import ua.aleh1s.usersservice.mapper.UserMapper;
import ua.aleh1s.usersservice.repository.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SubscriptionsClient subscriptionsClient;

    @Transactional
    public User getUserInfoOrCreate() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        UserEntity userEntity = userRepository.findUserEntityByUsername(jwt.getClaimAsString(ClaimsNames.USERNAME))
                .orElseGet(this::saveAndReturnNewUser);

        return userMapper.toUser(userEntity);
    }

    @Transactional
    public User updateUserInfo(UpdateUser updateUser) {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        UserEntity userEntity = getUserByUsername(
                jwt.getClaimAsString(ClaimsNames.USERNAME)
        );

        if (Objects.nonNull(updateUser.getGivenName())) {
            userEntity.setGivenName(updateUser.getGivenName());
        }

        if (Objects.nonNull(updateUser.getFamilyName())) {
            userEntity.setFamilyName(updateUser.getFamilyName());
        }

        if (Objects.nonNull(updateUser.getBio())) {
            userEntity.setBio(updateUser.getBio());
        }

        if (Objects.nonNull(updateUser.getAvatarUrl())) {
            userEntity.setAvatarUrl(updateUser.getAvatarUrl());
        }

        if (Objects.nonNull(updateUser.getSocialMediaLinks())) {
            userEntity.setSocialMediaLinks(updateUser.getSocialMediaLinks());
        }

        userEntity = userRepository.save(userEntity);

        return userMapper.toUser(userEntity);
    }

    public UserEntity getUserByUsername(String username) {
        return userRepository.findUserEntityByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }

    private UserEntity saveAndReturnNewUser() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        UserEntity userEntity = UserEntity.builder()
                .id(jwt.getClaimAsString(ClaimsNames.SUBJECT))
                .username(jwt.getClaimAsString(ClaimsNames.USERNAME))
                .email(jwt.getClaimAsString(ClaimsNames.EMAIL))
                .givenName(jwt.getClaimAsString(ClaimsNames.GIVEN_NAME))
                .familyName(jwt.getClaimAsString(ClaimsNames.FAMILY_NAME))
                .build();

        return userRepository.save(userEntity);
    }

    public UserProfile getUserProfileByUsername(String username) {
        UserEntity userEntity = getUserByUsername(username);

        Subscription activeSubscription;
        try {
            activeSubscription = subscriptionsClient.getActiveSubscription(userEntity.getId());
        } catch (FeignException.NotFound e) {
            activeSubscription = null;
        }

        return userMapper.toUserProfile(userEntity, activeSubscription);
    }

    public List<User> getUsersByUsernameLike(String usernameLike) {
        if (Objects.isNull(usernameLike) || usernameLike.isBlank()) {
            return List.of();
        }

        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = jwt.getClaimAsString(ClaimsNames.SUBJECT);

        return userRepository.findUserEntitiesByUsernameLike(usernameLike).stream()
                .filter(userEntity -> !userEntity.getId().equals(userId))
                .map(userMapper::toUser)
                .toList();
    }

    public List<User> getUsersByIds(Set<String> ids) {
        System.out.println(1);

        if (Objects.isNull(ids) || ids.isEmpty()) {
            return List.of();
        }

        System.out.println(2);

        return userRepository.findUserEntitiesByIdIn(ids).stream()
                .map(userMapper::toUser)
                .toList();
    }
}
