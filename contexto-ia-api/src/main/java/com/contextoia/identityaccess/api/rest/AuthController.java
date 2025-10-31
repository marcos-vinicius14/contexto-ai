package com.contextoia.identityaccess.api.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.contextoia.identityaccess.api.dto.AuthRequest;
import com.contextoia.identityaccess.api.dto.AuthResponse;
import com.contextoia.identityaccess.api.dto.UserDTO;
import com.contextoia.identityaccess.application.dto.CreateUserDTO;
import com.contextoia.identityaccess.application.service.AuthService;

/**
 * Controller REST para os endpoints públicos de autenticação (Login e
 * Registro).
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
    String token = authService.authenticate(request.username(), request.rawPassword());
    return ResponseEntity.ok(new AuthResponse(token));
  }

  @PostMapping("/register")
  public ResponseEntity<UserDTO> register(@RequestBody CreateUserDTO request) {
    UserDTO user = authService.register(request);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(user);
  }
}
