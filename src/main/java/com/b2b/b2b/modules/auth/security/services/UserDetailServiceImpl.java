package com.b2b.b2b.modules.auth.security.services;

import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.auth.entity.UserOrganization;
import com.b2b.b2b.modules.auth.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        User user = fetchUser(identifier);
        Integer defaultOrgId = user.getUserOrganizations().stream()
                .filter(UserOrganization::isDefaultHome)
                .map(uo -> uo.getOrganization().getOrganizationId())
                .findFirst()
                .orElseThrow(() -> new UsernameNotFoundException("User has no default home organization"));

        return UserDetailImpl.build(user, defaultOrgId);

    }

    @Transactional
    public UserDetails loadUserByUsernameAndOrg(String identifier, Integer orgId) throws UsernameNotFoundException {
        User user = fetchUser(identifier);
        boolean belongsToOrg = user.getUserOrganizations().stream()
                .anyMatch(uo -> uo.getOrganization().getOrganizationId().equals(orgId));
        if(!belongsToOrg) {
            log.info("Security Alert: User {} attempted to access unauthorized Org ID: {}", identifier, orgId);
            throw new UsernameNotFoundException("User does not belong to the specified organization.");
        }
        return UserDetailImpl.build(user, orgId);
    }

    private User fetchUser(String identifier) {
        return userRepository.findByUserName(identifier)
                .or(()-> userRepository.findByEmail(identifier))
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + identifier));
    }
}
