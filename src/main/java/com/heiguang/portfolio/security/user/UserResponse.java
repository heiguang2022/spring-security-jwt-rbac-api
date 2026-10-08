package com.heiguang.portfolio.security.user;

import java.time.Instant;

public record UserResponse(Long id, String email, Role role, boolean enabled, Instant createdAt) {
    public static UserResponse from(AppUser user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getRole(), user.isEnabled(), user.getCreatedAt());
    }
}
