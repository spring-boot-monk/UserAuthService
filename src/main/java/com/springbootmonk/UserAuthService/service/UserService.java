package com.springbootmonk.UserAuthService.service;

import com.springbootmonk.UserAuthService.dto.AuthResponse;
import com.springbootmonk.UserAuthService.dto.LoginRequest;
import com.springbootmonk.UserAuthService.dto.RegisterRequest;
import com.springbootmonk.UserAuthService.entity.UserEntity;
import com.springbootmonk.UserAuthService.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    public UserService(UserRepository userRepository, PasswordEncoder
                               passwordEncoder,
                       AuthenticationManager authenticationManager, JwtService
                               jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }
    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new RuntimeException("Username already taken");
        }
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email already in use");
        }
        UserEntity user = new UserEntity();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setProvider("LOCAL");
        user.setRoles("ROLE_USER");
        userRepository.save(user);
        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }
    public AuthResponse login(LoginRequest req) {
// Allows login by username or email
        String principal = req.getUsernameOrEmail();
        String username = principal;
        if (principal.contains("@")) {
            username = userRepository.findByEmail(principal)
                    .map(UserEntity::getUsername)
                    .orElseThrow(() -> new RuntimeException("Invalid Credentials"));
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, req.getPassword())
        );
        UserDetails user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }
}