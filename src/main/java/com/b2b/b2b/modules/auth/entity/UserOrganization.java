package com.b2b.b2b.modules.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "user_organization")
@FilterDef(
        name = "organizationFilter",
        parameters = @ParamDef(name = "orgId", type = Integer.class)
)
@Filter(
        name = "organizationFilter",
        condition = "organization_id = :orgId"
)
public class UserOrganization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_org_id")
    private Integer userOrganizationId;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private User user;
    @ManyToOne
    private Organization organization;
    @ManyToOne
    private Role role;
    private boolean isAccountOwner;//super admin of an organization, one that can remove sub admin
    private boolean isDefaultHome;
    public UserOrganization(User user, Organization organization, Role role, boolean isAccountOwner) {
        this.user = user;
        this.organization = organization;
        this.role = role;
        this.isAccountOwner = isAccountOwner;
    }

}
