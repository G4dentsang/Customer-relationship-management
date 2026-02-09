package com.b2b.b2b.modules.auth.service.impl;

import com.b2b.b2b.exception.BadRequestException;
import com.b2b.b2b.exception.ResourceNotFoundException;
import com.b2b.b2b.modules.auth.entity.RefreshToken;
import com.b2b.b2b.modules.auth.exception.*;
import com.b2b.b2b.modules.auth.payload.AuthResult;
import com.b2b.b2b.modules.auth.security.jwt.JwtUtils;
import com.b2b.b2b.modules.auth.security.request.LogInRequestDTO;
import com.b2b.b2b.modules.auth.security.response.LogInResponseDTO;
import com.b2b.b2b.modules.auth.security.services.UserDetailImpl;
import com.b2b.b2b.modules.auth.service.AuthMailService;
import com.b2b.b2b.modules.auth.service.AuthService;
import com.b2b.b2b.modules.auth.service.LoginAttemptService;
import com.b2b.b2b.modules.auth.service.RefreshTokenService;
import com.b2b.b2b.modules.notification.model.EmailVerificationToken;
import com.b2b.b2b.modules.notification.persistence.EmailVerificationTokenRepository;
import com.b2b.b2b.modules.organization.model.Organization;
import com.b2b.b2b.modules.organization.persistence.OrganizationRepository;
import com.b2b.b2b.modules.organization.persistence.UserOrganizationRepository;
import com.b2b.b2b.modules.user.model.User;
import com.b2b.b2b.modules.user.persistence.UserRepository;
import com.b2b.b2b.shared.util.AuthUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService
{
    private final UserRepository userRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final AuthMailService authMailService;
    private final LoginAttemptService loginAttemptService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;
    private final AuthUtil authUtil;
    private final OrganizationRepository organizationRepository;
    private final UserOrganizationRepository userOrganizationRepository;

    @Override
    public AuthResult processLogin(LogInRequestDTO request) {
        String identifier = request.getIdentifier();

        User user = userRepository.findByEmail(identifier)
                .or(() -> userRepository.findByUserName(identifier))
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!user.isAccountNonLocked()) {
            if (loginAttemptService.isAccUnlockedWhenTimeExpired(user)) {
                log.info("Account unlocked for user: {} ", identifier);
            } else {
                throw new AccountLockedException("Account is locked. Try again in 15 min.");
            }
        }

        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getIdentifier(), request.getPassword()));

            if(!user.isEmailVerified()){
                throw new EmailNotVerifiedException("Please verify your email address.") ;
            }

            loginAttemptService.loginSuccess(user);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetailImpl userDetails = (UserDetailImpl) authentication.getPrincipal();

            //Access JWT token
            ResponseCookie jwtCookie = jwtUtils.generateJwtCookies(userDetails);
            //Refresh token
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId(), userDetails.getOrganizationId());
            ResponseCookie jwtRefreshCookie = jwtUtils.generateRefreshJwtCookies(refreshToken.getToken());

            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            LogInResponseDTO response = new LogInResponseDTO(
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    roles,
                    userDetails.getOrganizationId()
            );

            return new AuthResult(jwtCookie,  jwtRefreshCookie, response);

        } catch (BadCredentialsException e) {
            loginAttemptService.loginFailed(user);
            throw e;
        }
    }

    @Transactional
    @Override
    public AuthResult processLogout(HttpServletRequest request) {
        Integer userIdToLogout = authUtil.loggedInUserId();

        if (userIdToLogout == null) {
            String refreshToken = jwtUtils.getJwtRefreshTokenFromCookies(request);
            if (refreshToken != null && !refreshToken.isEmpty()) {
                try {
                    RefreshToken token = refreshTokenService.findByToken(refreshToken);
                    userIdToLogout = token.getUser().getUserId();
                } catch (ResourceNotFoundException ex) {
                    log.warn("Logout attempted with invalid refresh token");
                    throw ex;
                }
            }
        }
        refreshTokenService.logoutAllSession(userIdToLogout);
        ResponseCookie cleanJwtCookie = jwtUtils.getCleanJwtCookie();
        ResponseCookie cleanRefreshCookie = jwtUtils.getCleanJwtRefreshCookie();

        return new AuthResult(cleanJwtCookie, cleanRefreshCookie, null);
    }

    @Override
    public AuthResult switchOrganization(Integer targetOrgId) {
        User user = authUtil.loggedInUser();
        Organization org = organizationRepository.findById(targetOrgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", targetOrgId));

        boolean isMember = userOrganizationRepository.existsByUserAndOrganization(user, org);
        if(isMember){
            throw new AccessDeniedException("Access denied: You are not a member of organization " + targetOrgId);
        }

        refreshTokenService.updateActiveOrg(user.getUserId(), targetOrgId);

        UserDetailImpl userDetails = UserDetailImpl.build(user, targetOrgId);
        ResponseCookie newJwtCookie = jwtUtils.generateJwtCookies(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        LogInResponseDTO responseDTO = new LogInResponseDTO(
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles,
                userDetails.getOrganizationId()
        );

        return new AuthResult(newJwtCookie, null, responseDTO);
    }

    @Transactional
    @Override
    public void verifyToken(String token) {
        EmailVerificationToken emailVerificationToken = emailVerificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("verification-token", "token", token));

        if (emailVerificationToken.isUsed()) {
            throw new InvalidTokenException("This link has already been used. Please log in.");
        }
        if (emailVerificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Verification link expired. Please request a new one.");
        }

        User user = emailVerificationToken.getUser();
        if (!user.isEmailVerified()) {
            user.setEmailVerified(true);
            user.setUserActive(true);
            userRepository.save(user);
        }

        emailVerificationToken.setUsed(true);
        emailVerificationTokenRepository.save(emailVerificationToken);
    }

    @Transactional
    @Override
    public void createAndSendVerificationCode(User user) {
        String token = UUID.randomUUID().toString();
        EmailVerificationToken emailVerificationToken = new EmailVerificationToken();
        emailVerificationToken.setToken(token);
        emailVerificationToken.setUser(user);
        emailVerificationToken.setUsed(false);
        emailVerificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));

        emailVerificationTokenRepository.save(emailVerificationToken);

        authMailService.sendVerificationEmail(user.getEmail(), token);

    }

    @Transactional
    @Override
    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        if(user.isEmailVerified()) {
            throw new BadRequestException("This account is already verified. Please log in.");
        }

        Optional<EmailVerificationToken> lastToken = emailVerificationTokenRepository.findFirstByUserOrderByCreatedAtDesc(user);
        if(lastToken.isPresent() && lastToken.get().getCreatedAt().isAfter(LocalDateTime.now().minusMinutes(5))) {
            throw new RateLimitExceededException("Please wait 5 minutes before requesting another link.");
        }
        createAndSendVerificationCode(user);
    }

    @Transactional
    @Override
    public AuthResult processRefreshToken(HttpServletRequest request) {
        String refreshToken = jwtUtils.getJwtRefreshTokenFromCookies(request);

        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new TokenRefreshException(null, "Refresh token is missing");
        }

        RefreshToken tokenFromDB = refreshTokenService.findByToken(refreshToken);
        refreshTokenService.verifyExpiration(tokenFromDB);

        UserDetailImpl userDetails = UserDetailImpl.build(tokenFromDB.getUser(), tokenFromDB.getOrganizationId());

        ResponseCookie newJwtCookie = jwtUtils.generateJwtCookies(userDetails);
        log.info("NEW Access Token: {}", newJwtCookie.toString());

        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(userDetails.getId(), userDetails.getOrganizationId());
        ResponseCookie newRefreshCookie = jwtUtils.generateRefreshJwtCookies(newRefreshToken.getToken());
        log.info("NEW Refresh Token: {}", newRefreshCookie.toString());

        return new AuthResult(newJwtCookie, newRefreshCookie, null);
    }

}
