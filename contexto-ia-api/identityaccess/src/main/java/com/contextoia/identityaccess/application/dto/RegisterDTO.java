package com.contextoia.identityaccess.application.dto;

public record RegisterDTO(
        String username,
        String email,
        String rawPassword
) {
}
