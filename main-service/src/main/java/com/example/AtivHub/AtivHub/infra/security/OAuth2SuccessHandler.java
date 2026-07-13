package com.example.AtivHub.AtivHub.infra.security;

import com.example.AtivHub.AtivHub.domain.user.User;
import com.example.AtivHub.AtivHub.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final TokenService tokenService;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        Optional<User> userOptional = userRepository.findByEmail(email);

        String targetUrl;
        if (userOptional.isPresent()) {
            // User exists, generate normal JWT token and redirect to frontend
            String token = tokenService.generateToken(userOptional.get());
            targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/auth/oauth2/callback")
                    .queryParam("token", token)
                    .build().toUriString();
        } else {
            // User does not exist, generate temp registration token and redirect to frontend to complete signup
            String tempToken = tokenService.generateRegisterToken(email, name);
            targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/auth/oauth2/callback")
                    .queryParam("tempToken", tempToken)
                    .queryParam("email", email)
                    .queryParam("name", name)
                    .build().toUriString();
        }

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        CookieUtils.deleteCookie(request, response, HttpCookieOAuth2AuthorizationRequestRepository.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
    }
}
