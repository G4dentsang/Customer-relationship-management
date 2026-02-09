package com.b2b.b2b.modules.auth.controller;

import com.b2b.b2b.modules.auth.payload.AuthResult;
import com.b2b.b2b.modules.user.payload.ResetPasswordRequest;
import com.b2b.b2b.modules.auth.security.request.LogInRequestDTO;
import com.b2b.b2b.modules.auth.service.*;
import com.b2b.b2b.shared.response.APIResponse;
import com.b2b.b2b.shared.response.MessageResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/app/v1/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/logIn")
    public ResponseEntity<?> logIn(@Valid @RequestBody LogInRequestDTO request) {
        AuthResult result = authService.processLogin(request);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, result.accessTokenCookie().toString())
                .header(HttpHeaders.SET_COOKIE, result.refreshTokenCookie().toString())
                .body(result.responseDTO());
    }

    @GetMapping("/verify-email")
    public ResponseEntity<APIResponse> verifyEmail(@RequestParam String token) {
         authService.verifyToken(token);
         return ResponseEntity.ok(new APIResponse("Email verified successfully, you can now log in.", true));
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<APIResponse> resendVerification(@RequestParam String email){
        authService.resendVerificationEmail(email);
        return ResponseEntity.ok(new APIResponse("If the email is valid, a new link has been sent.", true));
    }

    @PostMapping("/forget-password")
    public ResponseEntity<APIResponse> forgetPassword(@RequestParam String email){
        passwordResetService.requestPasswordReset(email);
        return ResponseEntity.accepted().body(new APIResponse("If a matching account exists, a reset link has been sent.", true));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<APIResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request){
        passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(new APIResponse("Password has been successfully reset. Please login", true));
    }

    @PostMapping("/refreshToken")
    @Transactional
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        AuthResult result = authService.processRefreshToken(request);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, result.accessTokenCookie().toString())
                .header(HttpHeaders.SET_COOKIE, result.refreshTokenCookie().toString())
                .body(new MessageResponse("Token Refreshed successful!"));
    }

    @PostMapping("/logOut")
    public ResponseEntity<?> logOut(HttpServletRequest request) {
        AuthResult result = authService.processLogout(request);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, result.accessTokenCookie().toString())
                .header(HttpHeaders.SET_COOKIE, result.refreshTokenCookie().toString())
                .body(new MessageResponse("You are successfully logged out"));
    }
}
