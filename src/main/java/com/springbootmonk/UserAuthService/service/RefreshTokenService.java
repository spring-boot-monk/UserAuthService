package com.springbootmonk.UserAuthService.service;

import com.springbootmonk.UserAuthService.entity.RefreshToken;
import com.springbootmonk.UserAuthService.entity.UserEntity;
import com.springbootmonk.UserAuthService.repository.RefreshTokenRepository;
import com.springbootmonk.UserAuthService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private UserRepository userRepository;

    public RefreshToken createRefreshToken(UserEntity user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiryDate(Instant.now().plusSeconds(7 * 24 * 60 * 60)) // 7 days
                .revoked(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken validateRefreshToken(UserEntity user) {
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findById(user.getId());
        if(refreshToken.isPresent() && !refreshToken.get().isRevoked() && refreshToken.get().getExpiryDate().isAfter(Instant.now())){
            return refreshToken.get();
        }
        return new RefreshToken(user.getId(), "EXPIRED", Instant.now(), true, user);
    }

    public void revokeToken(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(rt -> {
            rt.setRevoked(true);
            refreshTokenRepository.save(rt);
        });
    }
}
