package com.b2b.b2b.modules.auth.controller;

import com.b2b.b2b.modules.auth.security.jwt.JwtUtils;
import com.b2b.b2b.modules.auth.security.request.SignInRequestDTO;
import com.b2b.b2b.modules.auth.security.request.SignUpRequestDTO;
import com.b2b.b2b.modules.auth.security.response.MessageResponse;
import com.b2b.b2b.modules.auth.security.response.SignInResponseDTO;
import com.b2b.b2b.modules.auth.security.services.UserDetailImpl;
import com.b2b.b2b.modules.auth.service.AuthService;
import com.b2b.b2b.modules.auth.service.EmailVerificationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/app/v1/auth/")
public class AuthController {

     private final AuthService authService;
     private final EmailVerificationService emailVerificationService;
     private final AuthenticationManager authenticationManager;
     private final JwtUtils jwtUtils;
     private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(AuthService authService, EmailVerificationService emailVerificationService,
                          AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
         this.authService = authService;
         this.emailVerificationService = emailVerificationService;
         this.authenticationManager = authenticationManager;
         this.jwtUtils = jwtUtils;
    }

     @PostMapping("signIn")
     public ResponseEntity<?> signIn(@Valid @RequestBody SignInRequestDTO signInRequestDTO) {
        Authentication authentication;
         try{
             authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequestDTO.getIdentifier(), signInRequestDTO.getPassword()));

         }catch (AuthenticationException exception){
             Map<String, Object> map = new HashMap<>();
             map.put("message", "Bad credentials");
             map.put("status", false);
             return new ResponseEntity<Object>(map, HttpStatus.NOT_FOUND);
         }

         UserDetailImpl userDetails = (UserDetailImpl) authentication.getPrincipal();
         ResponseCookie jwtCookie = jwtUtils.generateJwtToken(userDetails);
         List<String> roles = userDetails.getAuthorities().stream()
                .map(role -> role.getAuthority())
                .toList();

         SignInResponseDTO signInResponse = new SignInResponseDTO(userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles);

         return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString()).body(signInResponse);
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
    @PostMapping("signOut")
    public ResponseEntity<?> signOut() {
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(new MessageResponse("You are successfully logged out"));
    }
    @GetMapping("user")
    public ResponseEntity<?> currentUserDetailsLoggedIn(Authentication authentication) {
        UserDetailImpl userDetails = (UserDetailImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item ->
                        item.getAuthority()).collect(Collectors.toList());

       SignInResponseDTO response = new SignInResponseDTO(userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles);

       return ResponseEntity.ok().body(response);

    }
}
