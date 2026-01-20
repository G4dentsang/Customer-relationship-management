package com.b2b.b2b.shared;

import com.b2b.b2b.exception.UnauthorizedException;
import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.auth.security.services.UserDetailImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {

    public User loggedInUser() {
        Authentication auth= SecurityContextHolder.getContext().getAuthentication();
        if(auth != null && auth.getPrincipal() instanceof UserDetailImpl userDetail) {
            return userDetail.getUser();
        }
        throw new UnauthorizedException("User session not found");
    }

    public String loggedInUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null && auth.getPrincipal() instanceof UserDetailImpl userDetail) {
            return userDetail.getEmail();
        }
        return null;
    }

    public Integer loggedInUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null && auth.getPrincipal() instanceof UserDetailImpl userDetail) {
            return userDetail.getId();
        }
        return null;
    }

}
