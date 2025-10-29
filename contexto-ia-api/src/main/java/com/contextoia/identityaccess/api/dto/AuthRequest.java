package com.contextoia.identityaccess.api.dto;

public record AuthRequest(
        String username,
        String email,
        String passwordRaw
){
}
