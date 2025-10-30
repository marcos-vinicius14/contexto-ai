package com.contextoia.identityaccess.application.service;

import com.contextoia.common.exceptions.AccountDisabledException;
import jakarta.transaction.Transactional;
import org.apache.http.auth.InvalidCredentialsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.contextoia.common.exceptions.InvalidDataException;
import com.contextoia.identityaccess.api.dto.UserDTO;
import com.contextoia.identityaccess.application.dto.CreateUserDTO;
import com.contextoia.identityaccess.domain.model.User;
import com.contextoia.identityaccess.domain.repository.UserRepository;
import com.contextoia.identityaccess.mapper.UserMapper;

import javax.security.auth.login.AccountLockedException;


@Service
public class AuthService {
    private static  final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public AuthService(AuthenticationManager authenticationManager,
            JwtService jwtService, UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    /**
     * Autentica um usuário e, se bem-sucedido, gera um token JWT.
     */
    public String authenticate(String username, String password) {
        validateCredentials(username, password);

        log.debug("Authentication attempt for user: {}", username);

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            if (!(authentication.getPrincipal() instanceof UserDetails userDetails)) {
                log.error("Principal is not UserDetails for user: {}", username);
                throw new IllegalStateException("Invalid principal type");
            }

            String token = jwtService.generateToken(userDetails);

            log.info("User authenticated successfully: {}", username);
            return token;

        } catch (BadCredentialsException e) {
            log.warn("Failed authentication attempt for user: {}", username);
            throw new InvalidDataException("Nome de usuário ou senha inválido");

        }
    }

    @Transactional
    public UserDTO register(CreateUserDTO request) {
        validateUsernameNotExists(request.username());
        validateEmailNotExists(request.email());

        User newUser = User.create(
                request.username(),
                request.email(),
                request.rawPassword(),
                passwordEncoder
        );

        User savedUser = userRepository.save(newUser);

        return userMapper.toDto(savedUser);
    }

    private void validateUsernameNotExists(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new InvalidDataException(
                    "Esse nome já esta em uso!"
            );
        }
    }

    private void validateEmailNotExists(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new InvalidDataException(
                    "Esse email já esta em uso!"
            );
        }
    }

    private void validateCredentials(String username, String password) {
        if (username == null || username.isBlank()) {
            throw  new InvalidDataException("Nome de usuário ou senha não pode ser nulo");
        }

        if (password == null || password.isBlank()) {
            throw  new InvalidDataException("Nome de usuário ou senha não pode ser nulo");
        }
    }

}
