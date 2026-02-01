package com.b2b.b2b.modules.auth.util;

public class HelperMethods {
    //String domainName,
    public static String getEmailVerificationToken(String token) {
        //later add domain name in prod and replace http://localhost:8081
        return String.format("http://localhost:8081/app/v1/auth/verify-email?token=%s" , token);
    }

    public static String getEmailResetPasswordToken(String token) {
        //later add domain name in prod and replace http://localhost:8081
        return String.format("http://localhost:8080/app/v1/auth/reset-password?token=%s", token);
    }
}
