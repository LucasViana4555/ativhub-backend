package com.example.AtivHub.AtivHub.service;

import com.example.AtivHub.AtivHub.domain.user.User;
import com.example.AtivHub.AtivHub.domain.user.dto.RegisterDTO;
import com.example.AtivHub.AtivHub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado."));
    }

    public void register(RegisterDTO data) {
        if (userRepository.findByEmail(data.email()).isPresent()) {
            throw new IllegalArgumentException("Email já cadastrado.");
        }

        String encryptedPassword = passwordEncoder.encode(data.password());
        
        User newUser = User.builder()
                .name(data.name())
                .email(data.email())
                .password(encryptedPassword)
                .role(data.role())
                .xp(0)
                .build();

        userRepository.save(newUser);
    }
}