package com.b2b.b2b.modules.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "user_organizations")
public class UserOrganization  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userOrganizationId;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    private Organization organization;
    @ManyToOne
    private Role role;
    private boolean isAccountOwner;//super admin of an organization, one that can remove sub admin
    private boolean isDefaultHome; // for user with multiple organization account, there 1st is default home
    public UserOrganization(User user, Organization organization, Role role, boolean isAccountOwner) {
        this.user = user;
        this.organization = organization;
        this.role = role;
        this.isAccountOwner = isAccountOwner;
    }

}
