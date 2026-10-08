package com.heiguang.portfolio.security.auth;

import com.heiguang.portfolio.security.user.UserResponse;

public record AuthResponse(String accessToken, String refreshToken, String tokenType, long expiresIn, UserResponse user) {
}
