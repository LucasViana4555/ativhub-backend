package com.example.AtivHub.AtivHub.controller;

import com.example.AtivHub.AtivHub.domain.user.User;
import com.example.AtivHub.AtivHub.domain.user.dto.LoginDTO;
import com.example.AtivHub.AtivHub.domain.user.dto.RegisterDTO;
import com.example.AtivHub.AtivHub.domain.user.dto.TokenDTO;
import com.example.AtivHub.AtivHub.infra.security.TokenService;
import com.example.AtivHub.AtivHub.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {


    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody @Valid LoginDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((User) auth.getPrincipal());

        return ResponseEntity.ok(new TokenDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterDTO data) {
        authService.register(data);
        return ResponseEntity.ok().build();
    }
}