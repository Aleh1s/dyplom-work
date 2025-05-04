package ua.aleh1s.usersservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.aleh1s.usersservice.domain.UserEntity;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findUserEntityByUsername(String username);
}
