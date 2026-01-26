package com.b2b.b2b.modules.auth.service;

import com.b2b.b2b.modules.auth.entity.AppRoles;
import com.b2b.b2b.modules.auth.payloads.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface UserManagementService {
    MessageResponse inviteMember(InviteMemberRequestDTO request);
    MemberResponseDTO acceptInvitation(AcceptInviteRequestDTO request);
    Page<MemberResponseDTO> getMembersByOrganization(UserFilterDTO filter, Pageable pageable);
    MemberResponseDTO getMemberByUserId(Integer userId);
    void updateRole(Integer userId, AppRoles role);
    void deactivateAndReassign(Integer userId, Integer successorId);
    void transferOwnerShip(Integer newOwnerId);
}
