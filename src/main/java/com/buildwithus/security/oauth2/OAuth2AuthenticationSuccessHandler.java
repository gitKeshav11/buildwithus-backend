package com.buildwithus.security.oauth2;

import com.buildwithus.auth.dto.AuthResponse;
import com.buildwithus.auth.service.AuthService;
import com.buildwithus.security.UserPrincipal;
import com.buildwithus.user.entity.User;
import com.buildwithus.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@Slf4j
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthService authService;
    private final UserRepository userRepository;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public OAuth2AuthenticationSuccessHandler(
            @Lazy AuthService authService,
            UserRepository userRepository
    ) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof UserPrincipal userPrincipal)) {
            log.error("OAuth2 success but principal is not UserPrincipal. Principal type: {}",
                    principal != null ? principal.getClass().getName() : "null");

            String errorUrl = UriComponentsBuilder
                    .fromUriString(frontendUrl)
                    .path("/oauth2/redirect")
                    .queryParam("error", "oauth2_principal_error")
                    .build()
                    .toUriString();

            clearAuthenticationAttributes(request);
            getRedirectStrategy().sendRedirect(request, response, errorUrl);
            return;
        }

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userPrincipal.getId()));

        AuthResponse authResponse = authService.processOAuth2User(user);

        String targetUrl = UriComponentsBuilder
                .fromUriString(frontendUrl)
                .path("/oauth2/redirect")
                .queryParam("token", authResponse.getAccessToken())
                .queryParam("refreshToken", authResponse.getRefreshToken())
                .build()
                .toUriString();

        clearAuthenticationAttributes(request);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}