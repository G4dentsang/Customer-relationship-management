package com.b2b.b2b.modules.auth.controller;

import com.b2b.b2b.config.AppConstants;
import com.b2b.b2b.modules.auth.entity.AppRoles;
import com.b2b.b2b.modules.auth.payloads.*;
import com.b2b.b2b.modules.auth.service.UserManagementService;
import com.b2b.b2b.shared.APIResponse;
import com.b2b.b2b.shared.AppResponse;
import com.b2b.b2b.shared.PaginatedResponse;
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
@RequestMapping("/app/v1/management/users")
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
    public ResponseEntity<AppResponse<PaginatedResponse<MemberResponseDTO>>> getAllUsers(UserFilterDTO filter, @PageableDefault(size = AppConstants.DEFAULT_SIZE, sort = "user.userName", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<MemberResponseDTO> userPage = userManagementService.getMembersByOrganization(filter, pageable);
        PaginatedResponse<MemberResponseDTO> data = new PaginatedResponse<>(userPage);
        return ResponseEntity.ok(new AppResponse<>(true, "Users retrieved successfully", data));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<MemberResponseDTO> getUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(userManagementService.getMemberByUserId(userId));
    }

    @PatchMapping("/{userId}/role")
    public ResponseEntity<APIResponse> updateRole(@PathVariable Integer userId, @RequestParam AppRoles role) {
        userManagementService.updateRole(userId, role);
        return ResponseEntity.ok(new APIResponse("Successfully updated role to : " + role, true));
    }

    @PostMapping("/{userId}/deactivate")
    public ResponseEntity<APIResponse> deactivateUser(@PathVariable Integer userId, @RequestBody DeactivateMemberRequestDTO request) {
        userManagementService.deactivateAndReassign(userId, request.getSuccessorId());
        return ResponseEntity.ok(new APIResponse("User successfully deactivated" , true));
    }

    @PostMapping("/transfer-org-owner")
    public ResponseEntity<APIResponse> transferAccOwner(@RequestParam Integer newOwnerId) {
        userManagementService.transferOwnerShip(newOwnerId);
        return ResponseEntity.ok(new APIResponse("Account Ownership successfully transferred", true));
    }

}
