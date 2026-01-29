package com.b2b.b2b.modules.auth.util;

public class HelperMethods {
    public static String getEmailVerificationToken(String domainName, String token) {
        //later add domain name in prod and replace http://localhost:8081
        return "http://localhost:8081/app/v1/auth/verify?token=%s" + token;
    }

    public static String getEmailResetPasswordToken(String domainName, String token) {
        //later add domain name in prod and replace http://localhost:8081
        return "http://localhost:8081/app/v1/auth/reset-password?token=%s" + token;
    }
}
