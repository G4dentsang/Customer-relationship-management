package com.b2b.b2b.modules.auth.controller;

import com.b2b.b2b.modules.auth.entity.AppRoles;
import com.b2b.b2b.modules.auth.payloads.*;
import com.b2b.b2b.modules.auth.security.services.UserDetailImpl;
import com.b2b.b2b.modules.auth.service.UserManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/management/users")
@Slf4j
@RequiredArgsConstructor
public class UserManagementController {

    private final UserManagementService userManagementService;

    @PostMapping("/invite")
    public ResponseEntity<MessageResponse> inviteMember(@Valid @RequestBody InviteMemberRequestDTO request, Authentication authentication) {
        UserDetailImpl currentUserDetail = (UserDetailImpl) authentication.getPrincipal();
        Integer authenticatedOrgId = currentUserDetail.getOrganizationId();
        return ResponseEntity.ok(userManagementService.inviteMember(request,authenticatedOrgId));
    }

    @PostMapping("/accept-invitation")
    public ResponseEntity<MemberResponseDTO> acceptInvitation(@Valid @RequestBody AcceptInviteRequestDTO request){
        return ResponseEntity.status(HttpStatus.CREATED).body(userManagementService.acceptInvitation(request));
    }

    @GetMapping
    public ResponseEntity<List<MemberResponseDTO>> getAllUsers(Authentication authentication) {
        UserDetailImpl currentUserDetail = (UserDetailImpl) authentication.getPrincipal();
        Integer authenticatedOrgId = currentUserDetail.getOrganizationId();
        return ResponseEntity.ok(userManagementService.getMembersByOrganizationId(authenticatedOrgId));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<MemberResponseDTO> getUser(@PathVariable Integer userId, Authentication authentication) {
        UserDetailImpl currentUserDetail = (UserDetailImpl) authentication.getPrincipal();
        Integer authenticatedOrgId = currentUserDetail.getOrganizationId();
        return ResponseEntity.ok(userManagementService.getMemberByUserId(userId, authenticatedOrgId));
    }

    @PatchMapping("/{userId}/role")
    public ResponseEntity<Void> updateRole(@PathVariable Integer userId, @RequestParam AppRoles role, Authentication authentication) {
        UserDetailImpl currentUserDetail = (UserDetailImpl) authentication.getPrincipal();
        Integer authenticatedOrgId = currentUserDetail.getOrganizationId();
        userManagementService.updateRole(userId, role, authenticatedOrgId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/{userId}/deactivate")
    public ResponseEntity<MessageResponse> deactivateUser(@PathVariable Integer userId, @RequestBody DeactivateMemberRequestDTO request, Authentication authentication) {
        UserDetailImpl currentUserDetail = (UserDetailImpl) authentication.getPrincipal();
        Integer authenticatedOrgId = currentUserDetail.getOrganizationId();
        userManagementService.deactivateAndReassign(userId, request.getSuccessorId(), authenticatedOrgId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/transfer-owner")
    public ResponseEntity<Void> transferAccOwner(@RequestParam Integer newOwnerId, Authentication authentication) {
        UserDetailImpl currentUserDetail = (UserDetailImpl) authentication.getPrincipal();
        Integer authenticatedOrgId = currentUserDetail.getOrganizationId();
        Integer currentOwnerId = currentUserDetail.getId();
        userManagementService.transferOwnerShip(newOwnerId, currentOwnerId, authenticatedOrgId);
        return  ResponseEntity.ok().build();
    }

}
