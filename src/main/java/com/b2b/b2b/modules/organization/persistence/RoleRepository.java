package com.b2b.b2b.modules.organization.persistence;

import com.b2b.b2b.modules.organization.model.AppRoles;
import com.b2b.b2b.modules.organization.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer>

{
    Optional<Role> findByAppRoles(AppRoles appRoles);
}
