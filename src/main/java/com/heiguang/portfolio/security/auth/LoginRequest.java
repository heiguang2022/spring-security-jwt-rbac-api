package com.heiguang.portfolio.security.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LoginRequest(
        @NotBlank
        @Pattern(regexp = "\\s*[^\\s@]+@[^\\s@]+\\.[^\\s@]+\\s*", message = "must be a well-formed email address")
        String email,
        @NotBlank String password
) {
}
