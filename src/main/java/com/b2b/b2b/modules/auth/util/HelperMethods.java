package com.b2b.b2b.modules.auth.util;

public class HelperMethods {
    public static String getEmailVerificationToken(String token) {
        return "http://localhost:8081/auth/verify-email?token=" + token;
    }
}
