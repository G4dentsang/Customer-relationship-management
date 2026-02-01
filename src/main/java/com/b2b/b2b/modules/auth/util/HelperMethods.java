package com.b2b.b2b.modules.auth.util;

public class HelperMethods {

    //String domainName, //later add domain name in prod and replace http://localhost:8081

    public static String getEmailVerificationLink(String token) {
        return String.format("http://localhost:8081/app/v1/auth/verify-email?token=%s", token);
    }

    //link to form
    public static String getEmailResetPasswordLink(String token) {
        return String.format("http://localhost:8080/app/v1/auth/reset-password-page?token=%s", token);
    }

    //link to form
    public static String getInvitationEmailLink(String token) {
        return String.format("http://localhost:8080/app/v1/management/users/accept-invitation-page?token=%s", token);
    }

}
