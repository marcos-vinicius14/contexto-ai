package com.contextoia.identityaccess.application.service;

import jakarta.transaction.Transactional;
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

    /**
     * Registers a new user based on the provided registration details.
     *
     * @param request the details required to create a new user, including email, username, and raw password
     * @return a DTO representing the newly created user, including their ID, username, and email
     * @throws InvalidDataException if the username or email already exists
     */
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

    /**
     * Validates that the given username does not already exist in the system.
     * If the username exists, an {@code InvalidDataException} is thrown.
     *
     * @param username the username to validate
     * @throws InvalidDataException if the username already exists
     */
    private void validateUsernameNotExists(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new InvalidDataException(
                    "Esse nome já esta em uso!"
            );
        }
    }

    /**
     * Validates that the given email does not already exist in the system.
     * If the email exists, an {@code InvalidDataException} is thrown.
     *
     * @param email the email to validate
     * @throws InvalidDataException if the email already exists
     */
    private void validateEmailNotExists(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new InvalidDataException(
                    "Esse email já esta em uso!"
            );
        }
    }

    /**
     * Validates that the provided username and password are not null or blank.
     * If either the username or password is invalid, an {@code InvalidDataException} will be thrown.
     *
     * @param username the username to validate
     * @param password the password to validate
     * @throws InvalidDataException if the username or password is null or blank
     */
    private void validateCredentials(String username, String password) {
        if (username == null || username.isBlank()) {
            throw  new InvalidDataException("Nome de usuário ou senha não pode ser nulo");
        }

        if (password == null || password.isBlank()) {
            throw  new InvalidDataException("Nome de usuário ou senha não pode ser nulo");
        }
    }

}
