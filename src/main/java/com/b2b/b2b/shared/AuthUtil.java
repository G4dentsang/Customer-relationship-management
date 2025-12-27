package com.b2b.b2b.shared;

import com.b2b.b2b.exception.APIException;
import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.auth.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {

    private final UserRepository userRepository;
    public AuthUtil(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User loggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUserName(authentication.getName())
                .orElseThrow(()-> new UsernameNotFoundException("User Not Found with username: " + authentication.getName()));
    }

    public String loggedInUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUserName(authentication.getName())
                .orElseThrow(()-> new UsernameNotFoundException("User Not Found with username: " + authentication.getName()));
        return user.getEmail();
    }

    public Integer loggedInUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUserName(authentication.getName())
                .orElseThrow(()-> new UsernameNotFoundException("User Not Found with username: " + authentication.getName()));
        return user.getUserId();
    }

    public Organization getPrimaryOrganization(User user) {
        return  user.getUserOrganizations()
                .stream()
                .filter((userOrganization -> userOrganization.isPrimary()))
                .findFirst()
                .orElseThrow(()-> new APIException("User has no primary organization"))
                .getOrganization();
    }
}
