package com.contextoia.identityaccess.domain.repository;

import com.contextoia.identityaccess.application.dto.UserAuthDetails;
import com.contextoia.identityaccess.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository  extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    Optional<UserAuthDetails> findAuthDetailsByUsername(String username);
}


