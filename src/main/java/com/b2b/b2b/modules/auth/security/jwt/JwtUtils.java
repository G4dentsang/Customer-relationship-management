package com.b2b.b2b.modules.auth.security.jwt;

import com.b2b.b2b.modules.auth.security.services.UserDetailImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;


@Component
@Slf4j
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;

    @Value("${spring.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    @Value("${spring.come.app.jwtCookieName}")
    private String jwtCookie;

    @Value("${spring.come.app.jwtRefreshCookieName}")
    private String jwtRefreshCookie;

    // --- Access Token Methods ---

    public String getJwtTokenFromCookies(HttpServletRequest request) {
        return getCookieValueByName(request, jwtCookie);
    }


    public ResponseCookie generateJwtCookies(UserDetailImpl userPrincipal) {
        String jwt = generateTokenFromUsername(userPrincipal.getUsername(), userPrincipal.getActiveOrganizationId());
        return generateCookie(jwtCookie, jwt, "/app/v1");

    }

    public String generateTokenFromUsername(String username,  Integer activeOrganizationId) {
        return Jwts.builder()
                .subject(username)
                .claim("activeOrgId", activeOrganizationId)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key())
                .compact();
    }

    // --- Refresh Token Methods ---

    public String getJwtRefreshTokenFromCookies(HttpServletRequest request) {
        return getCookieValueByName(request, jwtRefreshCookie);
    }

    public ResponseCookie generateRefreshJwtCookies(String refreshToken) {
        return generateCookie(jwtRefreshCookie, refreshToken, "/app/v1" );
    }

    // ---Cleaning Cookie ----

    public ResponseCookie getCleanJwtCookie() {
        return ResponseCookie.from(jwtCookie,"")
                .path("/app/v1")
                .maxAge(0)
                .httpOnly(true)
                .build();
    }

    public ResponseCookie getCleanJwtRefreshCookie() {
        return ResponseCookie.from(jwtRefreshCookie,"")
                .path("/app/v1")
                .maxAge(0)
                .httpOnly(true)
                .build();
    }

    // --- Helper Methods ---

    private String getCookieValueByName(HttpServletRequest request, String cookieName) {
        Cookie cookie = WebUtils.getCookie(request, cookieName);
        return (cookie != null) ? cookie.getValue() : null;
    }

    private ResponseCookie generateCookie(String name, String value, String path) {
        return ResponseCookie.from(name, value)
                .path(path)
                .maxAge(24 * 60 * 60) //1day
                .httpOnly(false) //true in prod
                .secure(false) //true in prod HTTPS
                .sameSite("Lax")// strict in prod
                .build();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public Integer getOrgIdFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("orgId", Integer.class);
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateJwtToken(String jwtToken) {
        try {
            Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(jwtToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;

    }
}
