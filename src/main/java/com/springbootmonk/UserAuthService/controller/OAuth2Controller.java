package com.springbootmonk.UserAuthService.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Tag(name = "OAuth", description = "OAuth APIs")
public class OAuth2Controller {
    @GetMapping("/oauth2/success")
    public ResponseEntity<Map<String, Object>> success() {
        return ResponseEntity.ok(Map.of(
                "message", "OAuth2 login successful",
                "next", "/"
        ));
    }
}