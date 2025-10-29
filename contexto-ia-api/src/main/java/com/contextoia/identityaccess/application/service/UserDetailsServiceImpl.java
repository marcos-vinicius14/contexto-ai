package com.contextoia.identityaccess.application.service;

import com.contextoia.identityaccess.application.dto.UserAuthDetails;
import com.contextoia.identityaccess.domain.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;


/**
 * Implementação da interface UserDetailsService do Spring Security.
 * Responsável por carregar os detalhes de autenticação do usuário a partir do banco de dados.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Tentativa de autenticação para o usuário: {}", username);

        UserAuthDetails authDetails;
        try {
            authDetails = userRepository.findAuthDetailsByUsername(username)
                    .orElseThrow(() -> {
                        log.warn("Falha na autenticação: Usuário não encontrado - {}", username);
                        return new UsernameNotFoundException("Usuário não encontrado: " + username);
                    });
        } catch (Exception e) {
            log.error("Erro ao buscar dados de autenticação para o usuário: {}", username, e);
            throw e;
        }

        log.debug("Usuário encontrado: {} | Habilitado: {} | Bloqueado: {}",
                username, authDetails.isEnabled(), authDetails.isLocked());

        if (!authDetails.isEnabled()) {
            log.warn("Tentativa de login com conta desabilitada: {}", username);
        }

        if (authDetails.isLocked()) {
            log.warn("Tentativa de login com conta bloqueada: {}", username);
        }


        log.info("Autenticação bem-sucedida para o usuário: {}", username);

        return User.builder()
                .username(authDetails.getUsername())
                .password(authDetails.getPasswordHash())
                .disabled(!authDetails.isEnabled())
                .accountLocked(authDetails.isLocked())
                .authorities(new ArrayList<>())
                .build();
    }
}