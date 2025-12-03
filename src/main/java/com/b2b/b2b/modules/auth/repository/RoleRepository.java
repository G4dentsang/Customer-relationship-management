package com.b2b.b2b.modules.auth.repository;

import com.b2b.b2b.modules.auth.entity.AppRoles;
import com.b2b.b2b.modules.auth.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer>

{
    Optional<Role> findByAppRoles(AppRoles appRoles);
}
