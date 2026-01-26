package com.b2b.b2b.modules.auth.controller;

import com.b2b.b2b.config.AppConstants;
import com.b2b.b2b.modules.auth.entity.AppRoles;
import com.b2b.b2b.modules.auth.payloads.*;
import com.b2b.b2b.modules.auth.service.UserManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/management/users")
@Slf4j
@RequiredArgsConstructor
public class UserManagementController {

    private final UserManagementService userManagementService;

    @PostMapping("/invite")
    public ResponseEntity<MessageResponse> inviteMember(@Valid @RequestBody InviteMemberRequestDTO request) {
        return ResponseEntity.ok(userManagementService.inviteMember(request));
    }

    @PostMapping("/accept-invitation")
    public ResponseEntity<MemberResponseDTO> acceptInvitation(@Valid @RequestBody AcceptInviteRequestDTO request){
        return ResponseEntity.status(HttpStatus.CREATED).body(userManagementService.acceptInvitation(request));
    }

    @GetMapping
    public ResponseEntity<Page<MemberResponseDTO>> getAllUsers(UserFilterDTO filter, @PageableDefault(size = AppConstants.DEFAULT_SIZE, sort = "username", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(userManagementService.getMembersByOrganization(filter, pageable));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<MemberResponseDTO> getUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(userManagementService.getMemberByUserId(userId));
    }

    @PatchMapping("/{userId}/role")
    public ResponseEntity<Void> updateRole(@PathVariable Integer userId, @RequestParam AppRoles role) {
        userManagementService.updateRole(userId, role);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/{userId}/deactivate")
    public ResponseEntity<Void> deactivateUser(@PathVariable Integer userId, @RequestBody DeactivateMemberRequestDTO request) {
        userManagementService.deactivateAndReassign(userId, request.getSuccessorId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/transfer-owner")
    public ResponseEntity<Void> transferAccOwner(@RequestParam Integer newOwnerId) {
        userManagementService.transferOwnerShip(newOwnerId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
