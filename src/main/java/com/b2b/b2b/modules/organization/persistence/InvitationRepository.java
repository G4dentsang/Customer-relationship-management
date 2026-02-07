package com.b2b.b2b.modules.organization.persistence;

import com.b2b.b2b.modules.organization.model.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvitationRepository extends JpaRepository<Invitation, Integer>
{
    Optional<Invitation> findByToken(String token);
}
