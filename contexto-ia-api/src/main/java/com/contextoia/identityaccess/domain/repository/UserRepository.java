package com.contextoia.identityaccess.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contextoia.identityaccess.application.dto.UserAuthDetails;
import com.contextoia.identityaccess.domain.model.User;

public interface UserRepository  extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    Optional<UserAuthDetails> findAuthDetailsByUsername(String username);
    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
