package com.b2b.b2b.modules.auth.controller;

import com.b2b.b2b.modules.auth.security.request.SignUpRequestDTO;
import com.b2b.b2b.modules.auth.security.response.MessageResponse;
import com.b2b.b2b.modules.auth.service.AuthService;
import com.b2b.b2b.modules.auth.service.EmailVerificationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/app/v1/auth/")
public class Authcontroller {

     private final AuthService authService;
     private final EmailVerificationService emailVerificationService;
     public Authcontroller(AuthService authService, EmailVerificationService emailVerificationService) {
         this.authService = authService;
         this.emailVerificationService = emailVerificationService;
     }

    @PostMapping("signUp")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequestDTO signUpRequestDTO) {
       authService.register(signUpRequestDTO);
       return ResponseEntity.ok(new MessageResponse("User registered successfully! Please verify your email."));
    }

    @GetMapping("verify")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
         emailVerificationService.verifyToken(token);
         return ResponseEntity.ok(new MessageResponse("Email verified successfully!"));

    }
}
