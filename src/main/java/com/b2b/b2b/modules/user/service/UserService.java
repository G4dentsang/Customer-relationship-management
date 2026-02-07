package com.b2b.b2b.modules.user.service;

import com.b2b.b2b.modules.organization.model.AppRoles;
import com.b2b.b2b.modules.auth.payload.*;
import com.b2b.b2b.modules.organization.payload.AcceptInviteRequestDTO;
import com.b2b.b2b.modules.organization.payload.InviteMemberRequestDTO;
import com.b2b.b2b.modules.organization.payload.MemberResponseDTO;
import com.b2b.b2b.modules.user.payload.UserFilterDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface UserService {
    MessageResponse inviteMember(InviteMemberRequestDTO request);
    MemberResponseDTO acceptInvitation(AcceptInviteRequestDTO request);
    Page<MemberResponseDTO> getMembersByOrganization(UserFilterDTO filter, Pageable pageable);
    MemberResponseDTO getMemberByUserId(Integer userId);
    void updateRole(Integer userId, AppRoles role);
    void deactivateAndReassign(Integer userId, Integer successorId);
    void transferOwnerShip(Integer newOwnerId);
}
