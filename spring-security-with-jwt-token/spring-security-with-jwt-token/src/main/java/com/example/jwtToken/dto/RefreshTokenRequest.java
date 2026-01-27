package com.example.jwtToken.dto;

import lombok.Data;

@Data
public class RefreshTokenRequest {
    private String refreshToken;
}
