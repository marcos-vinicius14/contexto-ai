package com.contextoia.identityaccess.api.dto;

import java.util.UUID;

public record UserDTO(
    UUID id,
    String username,
    String email
    ) {

}
