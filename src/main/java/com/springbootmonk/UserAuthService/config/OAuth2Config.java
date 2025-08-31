package com.springbootmonk.UserAuthService.config;

import com.springbootmonk.UserAuthService.service.CustomOAuth2UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

@Configuration
public class OAuth2Config {
    @Bean
    public CustomOAuth2UserService customOAuth2UserService() {
        return new CustomOAuth2UserService();
    }
    /**
     * Minimal success handler that redirects with a short message.
     * Replace with JWT redirect to your frontend if desired.
     */
    @Bean
    public AuthenticationSuccessHandler oAuth2SuccessHandler() {
        return (request, response, authentication) -> handleSuccess(request, response);
    }


    private void handleSuccess(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // You can issue a JWT here and redirect to your frontend with the token as a query param.
        response.sendRedirect("/oauth2/success");
    }
}