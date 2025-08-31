package com.springbootmonk.UserAuthService.controller;

import com.springbootmonk.UserAuthService.dto.AuthResponse;
import com.springbootmonk.UserAuthService.dto.LoginRequest;
import com.springbootmonk.UserAuthService.dto.RegisterRequest;
import com.springbootmonk.UserAuthService.entity.RefreshToken;
import com.springbootmonk.UserAuthService.entity.UserEntity;
import com.springbootmonk.UserAuthService.repository.UserRepository;
import com.springbootmonk.UserAuthService.service.JwtService;
import com.springbootmonk.UserAuthService.service.RefreshTokenService;
import com.springbootmonk.UserAuthService.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Auth APIs")
public class AuthController {
    @Autowired
    private UserService userService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Validated @RequestBody
                                                 RegisterRequest req) {
        return ResponseEntity.ok(userService.register(req));
    }
    @PostMapping("/login")
    public Map<String, String> login(@Validated @RequestBody LoginRequest body) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(body.getUsernameOrEmail(), body.getPassword())
        );

        UserEntity user = userRepository.findByUsername(body.getUsernameOrEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String accessToken = jwtService.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        return Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken.getToken());
    }

    @Operation(summary = "Logout user", description = "Revokes the provided refresh token")
    @PostMapping("/logout")
    public Map<String, String> logout(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        refreshTokenService.revokeToken(refreshToken);
        return Map.of("message", "Logged out successfully");
    }
}