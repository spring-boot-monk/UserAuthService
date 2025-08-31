package com.springbootmonk.UserAuthService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private String userName;
    public AuthResponse(String accessToken) { this.accessToken = accessToken; }
}