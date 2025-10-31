package com.contextoia.identityaccess.mapper;


import com.contextoia.identityaccess.api.dto.UserDTO;
import com.contextoia.identityaccess.domain.model.User;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
public class UserMapper {
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public UserDTO toDto(User user) {

        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }

    public static Optional<User> toEntity(UserDTO dto) {
        if (dto == null) {
            return Optional.empty();
        }

        User user = User.createMinimal(dto.id(), dto.username(), dto.email());

        return Optional.of(user);
    }

}
