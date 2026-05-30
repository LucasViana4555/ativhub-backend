package com.example.AtivHub.AtivHub.controller;

import com.example.AtivHub.AtivHub.domain.user.User;
import com.example.AtivHub.AtivHub.domain.user.dto.LoginDTO;
import com.example.AtivHub.AtivHub.domain.user.dto.ProfessorRegistrationRequest;
import com.example.AtivHub.AtivHub.domain.user.dto.StudentRegistrationRequest;
import com.example.AtivHub.AtivHub.domain.user.dto.TokenDTO;
import com.example.AtivHub.AtivHub.domain.user.dto.OAuth2CompleteRegistrationDTO;
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

// Recriando o arquivo para forçar a reindexação pela IDE
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

    @PostMapping("/register/professor")
    public ResponseEntity<Void> registerProfessor(@RequestBody @Valid ProfessorRegistrationRequest data) {
        authService.registerProfessor(data);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register/student")
    public ResponseEntity<Void> registerStudent(@RequestBody @Valid StudentRegistrationRequest data) {
        authService.registerStudent(data);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody java.util.Map<String, Object> payload) {
        String roleStr = (String) payload.get("role");
        if ("PROFESSOR".equalsIgnoreCase(roleStr)) {
            String name = (String) payload.get("name");
            String email = (String) payload.get("email");
            String password = (String) payload.get("password");
            
            String schoolName = payload.containsKey("schoolName") ? (String) payload.get("schoolName") : 
                               payload.containsKey("school") ? (String) payload.get("school") : 
                               (String) payload.get("escola");
                               
            String subject = payload.containsKey("subject") ? (String) payload.get("subject") : 
                             payload.containsKey("disciplina") ? (String) payload.get("disciplina") : 
                             (String) payload.get("disciplina");
            
            ProfessorRegistrationRequest request = new ProfessorRegistrationRequest(name, email, password, schoolName, subject);
            authService.registerProfessor(request);
        } else if ("STUDENT".equalsIgnoreCase(roleStr) || "ALUNO".equalsIgnoreCase(roleStr)) {
            String name = (String) payload.get("name");
            String email = (String) payload.get("email");
            String password = (String) payload.get("password");
            
            String roomCode = payload.containsKey("roomCode") ? (String) payload.get("roomCode") : 
                              payload.containsKey("codigoSala") ? (String) payload.get("codigoSala") : 
                              (String) payload.get("codigoSala");
            
            StudentRegistrationRequest request = new StudentRegistrationRequest(name, email, password, roomCode);
            authService.registerStudent(request);
        } else {
            throw new IllegalArgumentException("Perfil (role) inválido.");
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/oauth2/complete-registration")
    public ResponseEntity<TokenDTO> completeOAuth2Registration(@RequestBody @Valid OAuth2CompleteRegistrationDTO data) {
        User user = authService.completeOAuth2Registration(data);
        String token = tokenService.generateToken(user);
        return ResponseEntity.ok(new TokenDTO(token));
    }
}