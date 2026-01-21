package com.b2b.b2b.modules.auth.security.services;

import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.auth.entity.UserOrganization;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class UserDetailImpl implements UserDetails {
    @Serial
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String userName;
    private String email;
    @JsonIgnore //ignore the password during serialization into JSON format
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private Integer organizationId;
    private User user;

    public UserDetailImpl(Integer id, String userName, String email, String password, Collection<? extends GrantedAuthority> authorities, Integer organizationId, User user) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
        this.organizationId = organizationId;
        this.user = user;
    }

    public static UserDetailImpl build(User user) {
        List<GrantedAuthority> grantedAuthorities = user.getUserOrganizations()
                                                    .stream()
                                                    .map(UserOrganization::getRole)
                                                    .map(role -> new SimpleGrantedAuthority(role.getAppRoles().name()))
                                                    .collect(Collectors.toList());
        Integer orgId = user.getUserOrganizations()
                .stream()
                .map(uo -> uo.getOrganization().getOrganizationId())
                .findFirst()
                .orElse(null);

        return new UserDetailImpl(
                  user.getUserId(),
                  user.getUserName(),
                  user.getEmail(),
                  user.getPassword(),
                  grantedAuthorities,
                  orgId,
                  user);

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
