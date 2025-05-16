package ua.aleh1s.usersservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ua.aleh1s.usersservice.domain.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findUserEntityByUsername(String username);

    @Query("select u from UserEntity u where u.username like %:usernameLike%")
    List<UserEntity> findUserEntitiesByUsernameLike(String usernameLike);

    List<UserEntity> findUserEntitiesByIdIn(Set<String> ids);
}
