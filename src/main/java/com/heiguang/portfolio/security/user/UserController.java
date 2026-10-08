package com.heiguang.portfolio.security.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserRepository repository;

    public UserController(UserRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/profile")
    @Operation(summary = "Get the authenticated user's profile")
    public UserResponse profile(Authentication authentication) {
        return repository.findByEmailIgnoreCase(authentication.getName())
                .map(UserResponse::from)
                .orElseThrow(() -> new UserNotFoundException(authentication.getName()));
    }

    @GetMapping("/admin/users")
    @Operation(summary = "List all users (ADMIN only)")
    public List<UserResponse> users() {
        return repository.findAll().stream().map(UserResponse::from).toList();
    }
}
