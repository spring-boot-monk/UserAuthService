package com.springbootmonk.UserAuthService.service;

import com.springbootmonk.UserAuthService.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    private final SecretKey key;
    private final JwtConfig cfg;
    public JwtService(SecretKey key, JwtConfig cfg) {
        this.key = key; this.cfg = cfg;
    }
    @PostConstruct
    void checkKeyLength() {
    }
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        final Claims claims = parseAllClaims(token);
        return resolver.apply(claims);
    }
    public String generateToken(UserDetails userDetails) {
        return generateToken(Map.of("roles",
                userDetails.getAuthorities().toString()), userDetails);
    }
    public String generateToken(Map<String, Object> extraClaims, UserDetails
            userDetails) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + cfg.getExpiration()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !
                isTokenExpired(token);
    }
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    private Claims parseAllClaims(String token) {
        return
                Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
}
