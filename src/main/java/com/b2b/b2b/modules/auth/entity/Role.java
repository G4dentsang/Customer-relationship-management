package com.b2b.b2b.modules.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name="roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="role_id")
    private Integer roleId;
    @Enumerated(EnumType.STRING)
    private AppRoles appRoles;
    @OneToMany(mappedBy = "role")
    private List<UserOrganization> userOrganizations = new ArrayList<>();
}
