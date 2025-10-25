package com.contextoia.identityaccess.application.service;

import com.contextoia.identityaccess.application.dto.AuthDTO;
import com.contextoia.identityaccess.application.dto.RegisterDTO;
import com.contextoia.identityaccess.domain.model.User;
import com.contextoia.identityaccess.domain.repository.UserRepository;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager, UserDetailsService userDetailsService, JwtService jwtService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
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
                new UsernamePasswordAuthenticationToken(username, password)
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return  jwtService.generateToken(userDetails);
    }

    public User register(String username, String email, String rawPassword) {
        User newUser = User.create(
                username,
                email,
                rawPassword,
                passwordEncoder
        );

        return userRepository.save(newUser);

    }


}
