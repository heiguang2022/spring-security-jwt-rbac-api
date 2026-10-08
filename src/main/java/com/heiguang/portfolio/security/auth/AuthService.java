package com.heiguang.portfolio.security.auth;

import com.heiguang.portfolio.security.user.AppUser;
import com.heiguang.portfolio.security.user.Role;
import com.heiguang.portfolio.security.user.UserRepository;
import com.heiguang.portfolio.security.user.UserResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Locale;

@Service
public class AuthService {

    private final UserRepository users;
    private final RefreshTokenRepository refreshTokens;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthService(UserRepository users,
                       RefreshTokenRepository refreshTokens,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       TokenService tokenService) {
        this.users = users;
        this.refreshTokens = refreshTokens;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.email());
        if (users.existsByEmailIgnoreCase(email)) {
            throw new DuplicateEmailException(email);
        }
        AppUser user = new AppUser(email, passwordEncoder.encode(request.password()), Role.USER);
        try {
            return issueTokenPair(users.saveAndFlush(user));
        } catch (DataIntegrityViolationException exception) {
            // 唯一约束兜底，避免并发注册绕过应用层检查。
            throw new DuplicateEmailException(email);
        }
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        String email = normalizeEmail(request.email());
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, request.password()));
        AppUser user = users.findByEmailIgnoreCase(email).orElseThrow();
        return issueTokenPair(user);
    }

    @Transactional
    public AuthResponse refresh(RefreshRequest request) {
        Instant now = Instant.now();
        RefreshToken current = refreshTokens.findByTokenHash(tokenService.hashRefreshToken(request.refreshToken()))
                .filter(token -> token.isActive(now))
                .orElseThrow(InvalidRefreshTokenException::new);
        current.revoke(now);
        // 每次刷新都轮换令牌，旧令牌立即失效，降低泄露后的重放风险。
        return issueTokenPair(current.getUser());
    }

    @Transactional
    public void logout(String email, RefreshRequest request) {
        RefreshToken token = refreshTokens.findByTokenHash(tokenService.hashRefreshToken(request.refreshToken()))
                .orElseThrow(InvalidRefreshTokenException::new);
        if (!token.getUser().getEmail().equalsIgnoreCase(email) || !token.isActive(Instant.now())) {
            throw new InvalidRefreshTokenException();
        }
        token.revoke(Instant.now());
    }

    private AuthResponse issueTokenPair(AppUser user) {
        String rawRefreshToken = tokenService.createRefreshToken();
        refreshTokens.save(new RefreshToken(
                user,
                tokenService.hashRefreshToken(rawRefreshToken),
                tokenService.refreshTokenExpiry()
        ));
        return new AuthResponse(
                tokenService.createAccessToken(user),
                rawRefreshToken,
                "Bearer",
                tokenService.accessTokenExpiresInSeconds(),
                UserResponse.from(user)
        );
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
