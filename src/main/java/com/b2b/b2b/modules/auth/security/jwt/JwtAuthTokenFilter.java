package com.b2b.b2b.modules.auth.security.jwt;

import com.b2b.b2b.modules.auth.security.services.UserDetailServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
@RequiredArgsConstructor
@Slf4j
///* the class intercepts jwt access & refresh token  every request,
/// sets the new authentication to securityContextHolder
public class JwtAuthTokenFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final UserDetailServiceImpl userDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {

            String path = request.getServletPath();
            ///***** let endpoint pass jwt validation
            if (path.equals("/app/v1/auth/register-organization") ||
                    path.equals("/app/v1/auth/logIn") ||
                    path.equals("/app/v1/auth/forget-password") ||
                    path.equals("/app/v1/auth/verify-email") ||
                    path.equals("/app/v1/auth/refresh-token") ||
                    path.equals("/app/v1/auth/reset-password") ||
                    path.equals("/app/v1/auth/resend-verification"))
            {
                filterChain.doFilter(request, response);
                return;
            }

            String jwt = parseJwt(request);

            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {

                String username = jwtUtils.getUserNameFromJwtToken(jwt);
                Integer orgId = jwtUtils.getOrgIdFromJwtToken(jwt);

                UserDetails userDetails = userDetailsService.loadUserByUsernameAndOrg(username, orgId);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        } catch (Exception ex) {
            logger.error("Cannot authenticate user autToken", ex);
        }
        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        return jwtUtils.getJwtTokenFromCookies(request);
    }
}
