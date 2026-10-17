package com.k955.kdex.security.oauth2;

import com.k955.kdex.entity.User;
import com.k955.kdex.repository.UserRepository;
import com.k955.kdex.security.JwtAuthUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtAuthUtil jwtAuthUtil;
    private final UserRepository userRepository;

    @Value("${app.oauth2.success-redirect-url:http://localhost:3000/oauth2/redirect}")
    private String successRedirectUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = (String) oAuth2User.getAttribute("email");
        if (email == null || email.isBlank()) {
            response.sendRedirect(UriComponentsBuilder.fromUriString(successRedirectUrl)
                    .queryParam("error", URLEncoder.encode("Email not found in OAuth2 response", StandardCharsets.UTF_8))
                    .build().toUriString());
            return;
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found after OAuth2 authentication"));

        String token = jwtAuthUtil.generateToken(user);
        String redirectUri = UriComponentsBuilder.fromUriString(successRedirectUrl)
                .queryParam("token", token)
                .queryParam("email", URLEncoder.encode(user.getEmail(), StandardCharsets.UTF_8))
                .queryParam("name", URLEncoder.encode(user.getFullName(), StandardCharsets.UTF_8))
                .build().toUriString();
        response.sendRedirect(redirectUri);
    }
}
