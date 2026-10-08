package com.heiguang.portfolio.security.user;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String email) {
        super("User '" + email + "' was not found");
    }
}
