package com.b2b.b2b.modules.auth.controller;

import com.b2b.b2b.exception.ResourceNotFoundException;
import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.auth.entity.RefreshToken;
import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.auth.payloads.ResetPasswordRequest;
import com.b2b.b2b.modules.auth.repository.OrganizationRepository;
import com.b2b.b2b.modules.auth.repository.UserOrganizationRepository;
import com.b2b.b2b.modules.auth.repository.UserRepository;
import com.b2b.b2b.modules.auth.security.jwt.JwtUtils;
import com.b2b.b2b.modules.auth.security.request.LogInRequestDTO;
import com.b2b.b2b.modules.auth.security.request.SignUpRequestDTO;
import com.b2b.b2b.modules.auth.service.*;
import com.b2b.b2b.shared.APIResponse;
import com.b2b.b2b.shared.AuthUtil;
import com.b2b.b2b.shared.MessageResponse;
import com.b2b.b2b.modules.auth.security.response.SignInResponseDTO;
import com.b2b.b2b.modules.auth.security.services.UserDetailImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/app/v1/auth/")
@Slf4j
@RequiredArgsConstructor
public class AuthController {

     private final AuthService authService;
     private final EmailService emailService;
     private final AuthenticationManager authenticationManager;
     private final JwtUtils jwtUtils;
     private final PasswordResetService passwordResetService;
    private final UserRepository userRepository;
    private final LoginAttemptService loginAttemptService;
    private final RefreshTokenService refreshTokenService;
    private final UserOrganizationRepository userOrganizationRepository;
    private final AuthUtil authUtil;
    private final OrganizationRepository organizationRepository;

    @PostMapping("logIn")
    public ResponseEntity<?> logIn(@Valid @RequestBody LogInRequestDTO request) {
        String identifier = request.getIdentifier();

        User user = userRepository.findByEmail(identifier)
                .or(() -> userRepository.findByUserName(identifier))
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!user.isAccountNonLocked()) {
            if (loginAttemptService.isAccUnlockedWhenTimeExpired(user)) {
                log.info("Account unlocked for user: {} ", identifier);
            } else {
                return ResponseEntity.status(HttpStatus.LOCKED).body(new MessageResponse("Account is locked due to more than 5 many failed attempts. Try again later in 15min."));
            }
        }

        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getIdentifier(), request.getPassword()));
            loginAttemptService.loginSuccess(user);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetailImpl userDetails = (UserDetailImpl) authentication.getPrincipal();
            //Access JWT token
            ResponseCookie jwtCookie = jwtUtils.generateJwtCookies(userDetails);

            //Refresh token
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId(), userDetails.getActiveOrganizationId());
            ResponseCookie jwtRefreshCookie = jwtUtils.generateRefreshJwtCookies(refreshToken.getToken());

            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            SignInResponseDTO response = new SignInResponseDTO(
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    roles,
                    userDetails.getActiveOrganizationId()
            );

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
                    .body(response);

        } catch (BadCredentialsException e) {
            loginAttemptService.loginFailed(user);
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        String refreshToken = jwtUtils.getJwtRefreshTokenFromCookies(request);

        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Refresh token is missing"));
        }

        RefreshToken tokenFromDB = refreshTokenService.findByToken(refreshToken);
        refreshTokenService.verifyExpiration(tokenFromDB);

        UserDetailImpl userDetails = UserDetailImpl.build(tokenFromDB.getUser(), tokenFromDB.getCurrentActiveOrgId());
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookies(userDetails);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(new MessageResponse("Token refreshed for Org ID: " + tokenFromDB.getCurrentActiveOrgId()));
    }


    @PostMapping("register-organization")
    public ResponseEntity<APIResponse> registerOrganization(@Valid @RequestBody SignUpRequestDTO request) {
       authService.registerOrganizationAndAdmin(request);
       return ResponseEntity
               .status(HttpStatus.CREATED)
               .body(new APIResponse("Organization created successfully with you as admin. Please verify email before login.",true));
    }

    @PostMapping("verify-email")
    public ResponseEntity<APIResponse> verifyEmail(@RequestParam String token) {
         emailService.verifyToken(token);
         return ResponseEntity.ok(new APIResponse("Email verified successfully, you can now log in.", true));
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<APIResponse> resendVerification(@RequestParam String email){
        emailService.resendVerificationEmail(email);
        return ResponseEntity.ok(new APIResponse("If the email is valid, a new link has been sent.", true));
    }

    @PostMapping("/forget-password")
    public ResponseEntity<APIResponse> forgetPassword(@RequestParam String email){
        passwordResetService.requestPasswordReset(email);
        return ResponseEntity.accepted().body(new APIResponse("Password reset link has been send, if email is valid.", true));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<APIResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request){
        passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(new APIResponse("Password has been successfully reset.", true));
    }

    @PostMapping("/switch-org/{orgId}")
    public ResponseEntity<?> switchOrg(@PathVariable("orgId") Integer orgId) {
        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", orgId));

        userOrganizationRepository.findByUserAndOrganization(authUtil.loggedInUser(), org)
                .orElseThrow(() -> new AccessDeniedException("Access denied to organisation"));

        refreshTokenService.updateActiveOrg(authUtil.loggedInUserId(), orgId);

        UserDetailImpl userDetails = UserDetailImpl.build(authUtil.loggedInUser(), orgId);
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookies(userDetails);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(new MessageResponse("Switched to organization: " + orgId));
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
                .map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        SignInResponseDTO response = new SignInResponseDTO(userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles, userDetails.getActiveOrganizationId());

        return ResponseEntity.ok().body(response);

    }
}
