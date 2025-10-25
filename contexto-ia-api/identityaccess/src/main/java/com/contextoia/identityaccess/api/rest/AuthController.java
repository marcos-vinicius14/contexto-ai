package com.contextoia.identityaccess.api.rest;

import com.contextoia.identityaccess.api.dto.AuthRequest;
import com.contextoia.identityaccess.api.dto.AuthResponse;
import com.contextoia.identityaccess.api.dto.RegisterRequest;
import com.contextoia.identityaccess.application.service.AuthService;
import com.contextoia.identityaccess.domain.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller REST para os endpoints públicos de autenticação (Login e Registro).
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        String token = authService.authenticateAndGetToken(request.username(), request.passwordRaw());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            User newUser = authService.register(request.username(), request.email(), request.password());
            return ResponseEntity.ok("Usuário " + newUser.getUsername() + " registrado com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
