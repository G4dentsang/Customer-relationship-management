package com.b2b.b2b.exception;

public class TokenRefreshException extends RuntimeException {
    public TokenRefreshException(String token, String message) {
        super(String.format(token, message));
    }
}
