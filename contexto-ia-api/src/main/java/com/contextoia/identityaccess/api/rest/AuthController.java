package com.contextoia.identityaccess.api.rest;

import com.contextoia.identityaccess.application.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.contextoia.identityaccess.api.dto.AuthRequest;
import com.contextoia.identityaccess.api.dto.AuthResponse;
import com.contextoia.identityaccess.api.dto.UserDTO;
import com.contextoia.identityaccess.application.dto.CreateUserDTO;
import com.contextoia.identityaccess.application.service.AuthService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody AuthRequest request, HttpServletResponse response) {
        String token = authService.authenticate(request.username(), request.rawPassword());

        Cookie jwtCookie = new Cookie("jwt-token", token);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true); // Defina como 'true' em produção (requer HTTPS)
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(24 * 60 * 60); // Expira em 1 dia

        response.addCookie(jwtCookie);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody CreateUserDTO request) {
        return new ResponseEntity<>(authService.register(request), HttpStatus.CREATED);
    }

}


