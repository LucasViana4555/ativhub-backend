package com.example.AtivHub.AtivHub.service;

import com.example.AtivHub.AtivHub.domain.user.EmailVerificationToken;
import com.example.AtivHub.AtivHub.domain.user.User;
import com.example.AtivHub.AtivHub.repository.EmailVerificationTokenRepository;
import com.example.AtivHub.AtivHub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public void sendVerificationEmail(User user) {
        // remove token antigo se existir de forma segura
        tokenRepository.findByUser(user).ifPresent(token -> {
            tokenRepository.delete(token);
            tokenRepository.flush();
        });

        // gera codigo de 6 digitos
        String code = String.format("%06d", secureRandom.nextInt(1000000));

        // salva novo token q expira em 15 min
        EmailVerificationToken token = new EmailVerificationToken(code, user, 15);
        tokenRepository.save(token);

        // tenta mandar email real, senao simula no console
        boolean sentRealEmail = false;
        if (mailUsername != null && !mailUsername.trim().isEmpty()) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(user.getEmail());
                message.setSubject("Verifique seu e-mail no AtivHub");
                message.setText("Olá " + user.getName() + ",\n\n" +
                        "Obrigado por se cadastrar no AtivHub!\n" +
                        "Para ativar sua conta, utilize o código de verificação abaixo:\n\n" +
                        "              [  " + code + "  ]\n\n" +
                        "Este código é válido por 15 minutos.\n\n" +
                        "Equipe AtivHub");
                
                mailSender.send(message);
                sentRealEmail = true;
                System.out.println("[EmailVerificationService] E-mail real enviado com sucesso para: " + user.getEmail());
            } catch (Exception e) {
                System.err.println("Falha ao enviar e-mail real (usando fallback de simulação): " + e.getMessage());
            }
        }

        if (!sentRealEmail) {
            // simula envio de email no console
            sendSimulatedEmail(user.getEmail(), user.getName(), code);
        }
    }

    @Transactional
    public void verifyEmail(String email, String code) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com o e-mail informado."));

        if (user.isEmailVerified()) {
            throw new IllegalArgumentException("E-mail já está verificado.");
        }

        EmailVerificationToken token = tokenRepository.findByToken(code)
                .orElseThrow(() -> new IllegalArgumentException("Código de verificação inválido ou expirado."));

        if (!token.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Código de verificação inválido.");
        }

        if (token.isExpired()) {
            throw new IllegalArgumentException("Código de verificação expirou.");
        }

        // valida user e apaga o token
        user.setEmailVerified(true);
        userRepository.save(user);
        tokenRepository.delete(token);

        System.out.println("[EmailVerificationService] E-mail verificado com sucesso para: " + email);
    }

    private void sendSimulatedEmail(String toEmail, String name, String code) {
        System.out.println("\n=================================================");
        System.out.println("   [SIMULAÇÃO DE ENVIO DE E-MAIL - ATIVHUB]   ");
        System.out.println("=================================================");
        System.out.println("Para: " + toEmail);
        System.out.println("Destinatário: " + name);
        System.out.println("Assunto: Verifique seu e-mail no AtivHub");
        System.out.println("-------------------------------------------------");
        System.out.println("Olá " + name + ",");
        System.out.println("Obrigado por se cadastrar no AtivHub!");
        System.out.println("Para ativar sua conta, utilize o código de verificação abaixo:");
        System.out.println("\n              [  " + code + "  ]\n");
        System.out.println("Este código é válido por 15 minutos.");
        System.out.println("Se você não solicitou este cadastro, desconsidere este e-mail.");
        System.out.println("-------------------------------------------------");
        System.out.println("Equipe AtivHub");
        System.out.println("=================================================\n");
    }
}
