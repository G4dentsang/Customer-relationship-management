package com.b2b.b2b.modules.auth.service;

import com.b2b.b2b.modules.auth.entity.AppRoles;
import com.b2b.b2b.modules.auth.payloads.*;

import java.util.List;

public interface UserManagementService {
    MessageResponse inviteMember(InviteMemberRequestDTO request, Integer orgId);
    MemberResponseDTO acceptInvitation(AcceptInviteRequestDTO request);
    List<MemberResponseDTO> getMembersByOrganizationId(Integer orgId);
    MemberResponseDTO getMemberByUserId(Integer userId, Integer orgId);
    void updateRole(Integer userId, AppRoles role, Integer orgId);
    void deactivateAndReassign(Integer userId, Integer successorId, Integer orgId);
    void transferOwnerShip(Integer newOwnerId, Integer currentOwnerId, Integer orgId);
}
