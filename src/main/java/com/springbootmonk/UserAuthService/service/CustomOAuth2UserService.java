package com.springbootmonk.UserAuthService.service;

import com.springbootmonk.UserAuthService.entity.UserEntity;
import com.springbootmonk.UserAuthService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    @Autowired(required = false)
    private UserRepository userRepository;
    @Override
    public OAuth2User loadUser(OAuth2UserRequest req) throws
            OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(req);
        Map<String, Object> attrs = user.getAttributes();
        String registrationId = req.getClientRegistration().getRegistrationId();
        String email = resolveEmail(registrationId, attrs);
        String name = resolveName(registrationId, attrs);
        if (userRepository != null && email != null) {
            UserEntity entity = userRepository.findByEmail(email).orElseGet(() -> {
                    UserEntity u = new UserEntity();
            u.setEmail(email);
            u.setUsername(generateUsername(name, email));
            u.setProvider(registrationId.toUpperCase());
            u.setRoles("ROLE_USER");
            return userRepository.save(u);});
            return new DefaultOAuth2User(
                    Collections.singleton(new
                            SimpleGrantedAuthority("ROLE_USER")),
                    attrs,
                    "email"
            );
        }
        return user;
    }
    private String resolveEmail(String provider, Map<String, Object> attrs) {
        if ("github".equals(provider)) {
// GitHub may not return email unless scope user:email and verified email exists
            Object email = attrs.get("email");
            return email != null ? email.toString() : null;
        }
// Google, etc.
        Object email = attrs.get("email");
        return email != null ? email.toString() : null;
    }
    private String resolveName(String provider, Map<String, Object> attrs) {
        Object name = attrs.get("name");
        if (name != null) return name.toString();
        Object login = attrs.get("login");
        return login != null ? login.toString() : "user";
    }
    private String generateUsername(String name, String email) {
        if (name != null && !name.isBlank()) return name.replaceAll("\\s+",
                "").toLowerCase();
        return email.substring(0, email.indexOf('@'));
    }
}

