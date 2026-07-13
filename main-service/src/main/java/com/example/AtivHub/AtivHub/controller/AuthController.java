package com.example.AtivHub.AtivHub.controller;

import com.example.AtivHub.AtivHub.domain.user.User;
import com.example.AtivHub.AtivHub.domain.user.dto.LoginDTO;
import com.example.AtivHub.AtivHub.domain.user.dto.ProfessorRegistrationRequest;
import com.example.AtivHub.AtivHub.domain.user.dto.StudentRegistrationRequest;
import com.example.AtivHub.AtivHub.domain.user.dto.TokenDTO;
import com.example.AtivHub.AtivHub.domain.user.dto.OAuth2CompleteRegistrationDTO;
import com.example.AtivHub.AtivHub.domain.user.dto.VerifyEmailDTO;
import com.example.AtivHub.AtivHub.domain.user.dto.ResendVerificationDTO;
import com.example.AtivHub.AtivHub.infra.security.TokenService;
import com.example.AtivHub.AtivHub.service.AuthService;
import com.example.AtivHub.AtivHub.service.EmailVerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Endpoints para autenticação, registro e verificação de e-mail")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final AuthService authService;
    private final EmailVerificationService emailVerificationService;

    @PostMapping("/login")
    @Operation(summary = "Realiza o login", description = "Autentica o usuário e retorna o token JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
        @ApiResponse(responseCode = "401", description = "Credenciais inválidas"),
        @ApiResponse(responseCode = "403", description = "E-mail não verificado")
    })
    public ResponseEntity<TokenDTO> login(@RequestBody @Valid LoginDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);
        var token = tokenService.generateToken((User) auth.getPrincipal());
        return ResponseEntity.ok(new TokenDTO(token));
    }

    @PostMapping("/register/professor")
    @Operation(summary = "Registra um professor", description = "Cria uma nova conta de professor e retorna o token JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Professor registrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou e-mail já cadastrado")
    })
    public ResponseEntity<TokenDTO> registerProfessor(@RequestBody @Valid ProfessorRegistrationRequest data) {
        User professor = authService.registerProfessor(data);
        var token = tokenService.generateToken(professor);
        return ResponseEntity.ok(new TokenDTO(token));
    }

    @PostMapping("/register/student")
    @Operation(summary = "Registra um aluno", description = "Cria uma nova conta de aluno com e-mail não verificado, envia código de verificação e retorna o token JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Aluno registrado com sucesso, e-mail de verificação enviado e retorna o token JWT"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou e-mail já cadastrado")
    })
    public ResponseEntity<TokenDTO> registerStudent(@RequestBody @Valid StudentRegistrationRequest data) {
        User student = authService.registerStudent(data);
        var token = tokenService.generateToken(student);
        return ResponseEntity.ok(new TokenDTO(token));
    }

    @PostMapping("/register")
    @Operation(summary = "Registra um usuário (Professor ou Aluno)", description = "Endpoint genérico que cria um perfil baseado no campo role e retorna o token JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Registro realizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Role inválido, dados inválidos ou e-mail já cadastrado")
    })
    public ResponseEntity<TokenDTO> register(@RequestBody java.util.Map<String, Object> payload) {
        String roleStr = (String) payload.get("role");
        User user;
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
            user = authService.registerProfessor(request);
        } else if ("STUDENT".equalsIgnoreCase(roleStr) || "ALUNO".equalsIgnoreCase(roleStr)) {
            String name = (String) payload.get("name");
            String email = (String) payload.get("email");
            String password = (String) payload.get("password");
            
            String roomCode = payload.containsKey("roomCode") ? (String) payload.get("roomCode") : 
                              payload.containsKey("codigoSala") ? (String) payload.get("codigoSala") : 
                              (String) payload.get("codigoSala");
            
            StudentRegistrationRequest request = new StudentRegistrationRequest(name, email, password, roomCode);
            user = authService.registerStudent(request);
        } else {
            throw new IllegalArgumentException("Perfil (role) inválido.");
        }
        var token = tokenService.generateToken(user);
        return ResponseEntity.ok(new TokenDTO(token));
    }

    @PostMapping("/verify-email")
    @Operation(summary = "Verifica o e-mail do aluno", description = "Valida o código de verificação de 6 dígitos enviado por e-mail para ativar a conta")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "E-mail verificado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Código inválido, expirado ou e-mail já verificado")
    })
    public ResponseEntity<Void> verifyEmail(@RequestBody @Valid VerifyEmailDTO data) {
        emailVerificationService.verifyEmail(data.email(), data.code());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/resend-verification")
    @Operation(summary = "Reenvia o código de verificação de e-mail", description = "Gera um novo código de verificação e o envia por e-mail (simulado)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Novo código de verificação enviado com sucesso"),
        @ApiResponse(responseCode = "400", description = "E-mail inválido ou já verificado")
    })
    public ResponseEntity<Void> resendVerification(@RequestBody @Valid ResendVerificationDTO data) {
        var user = authService.findUserByEmail(data.email());
        if (user.isEmailVerified()) {
            throw new IllegalArgumentException("E-mail já está verificado.");
        }
        emailVerificationService.sendVerificationEmail(user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/oauth2/complete-registration")
    @Operation(summary = "Completa o registro via OAuth2", description = "Salva os dados adicionais necessários para o login social e gera o token JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Registro OAuth2 finalizado com sucesso e logado"),
        @ApiResponse(responseCode = "400", description = "Token inválido, expirado ou dados ausentes")
    })
    public ResponseEntity<TokenDTO> completeOAuth2Registration(@RequestBody @Valid OAuth2CompleteRegistrationDTO data) {
        User user = authService.completeOAuth2Registration(data);
        String token = tokenService.generateToken(user);
        return ResponseEntity.ok(new TokenDTO(token));
    }
}