package com.heiguang.portfolio.security.auth;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String email) {
        super("Email '" + email + "' is already registered");
    }
}
