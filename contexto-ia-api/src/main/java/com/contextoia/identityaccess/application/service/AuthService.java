package com.contextoia.identityaccess.application.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.contextoia.identityaccess.api.dto.UserDTO;
import com.contextoia.identityaccess.application.dto.CreateUserDTO;
import com.contextoia.identityaccess.domain.model.User;
import com.contextoia.identityaccess.domain.repository.UserRepository;
import com.contextoia.identityaccess.mapper.UserMapper;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager,
            JwtService jwtService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Autentica um usuário e, se bem-sucedido, gera um token JWT.
     */
    public String authenticateAndGetToken(String username, String password) {
        Authentication authentication = authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(username, password));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return jwtService.generateToken(userDetails);
    }

    public UserDTO register(CreateUserDTO request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new IllegalArgumentException("Username '" + request.username() + "' already exists.");
        }

        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Email" + request.email() + "' already registered.");
        }

        User newUser = User.create(
                request.username(),
                request.email(),
                request.rawPassword(),
                passwordEncoder);

        userRepository.save(newUser);
        UserDTO userCreated = UserMapper.INSTANCE.userToUserDto(newUser);

        return userCreated;

    }

}
