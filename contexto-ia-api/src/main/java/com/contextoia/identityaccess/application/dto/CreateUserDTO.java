package com.contextoia.identityaccess.application.dto;

public record CreateUserDTO(
    String email,
    String username,
    String rawPassword
) {

}
