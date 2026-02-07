package com.b2b.b2b.modules.auth.entity;

import com.b2b.b2b.shared.BaseEntity;
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
public class UserOrganization extends BaseEntity{

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    private Role role;

    //super admin of an organization, one that can remove sub admin
    private boolean isAccountOwner;
    // for user with multiple organization account, there 1st is default home
    private boolean isDefaultHome;

    public UserOrganization(User user, Organization organization, Role role, boolean isAccountOwner) {
        this.user = user;
        this.setOrganization(organization);
        this.role = role;
        this.isAccountOwner = isAccountOwner;
    }
}
