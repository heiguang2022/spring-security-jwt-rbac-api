package com.heiguang.portfolio.security.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Size(max = 190)
        @Pattern(regexp = "\\s*[^\\s@]+@[^\\s@]+\\.[^\\s@]+\\s*", message = "must be a well-formed email address")
        String email,
        @NotBlank @Size(min = 10, max = 72)
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$", message = "must include uppercase, lowercase, and a number")
        String password
) {
}
