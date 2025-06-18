package com.homeaid.auth.dto;

public record TokenResponse(
    String accessToken,
    String refreshToken
) {}

