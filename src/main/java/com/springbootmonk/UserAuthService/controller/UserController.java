package com.springbootmonk.UserAuthService.controller;

import com.springbootmonk.UserAuthService.entity.RefreshToken;
import com.springbootmonk.UserAuthService.entity.UserEntity;
import com.springbootmonk.UserAuthService.repository.UserRepository;
import com.springbootmonk.UserAuthService.service.RefreshTokenService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User", description = "User management APIs")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RefreshTokenService refreshTokenService;

    @GetMapping("/me")
    public RefreshToken getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return refreshTokenService.validateRefreshToken(user);
    }
}
