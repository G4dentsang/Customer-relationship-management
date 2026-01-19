package com.b2b.b2b.modules.auth.service.impl;

import com.b2b.b2b.exception.BadResuestException;
import com.b2b.b2b.exception.ResourceNotFoundException;
import com.b2b.b2b.exception.UnauthorizedException;
import com.b2b.b2b.modules.auth.entity.*;
import com.b2b.b2b.modules.auth.payloads.*;
import com.b2b.b2b.modules.auth.repository.*;
import com.b2b.b2b.modules.auth.service.UserManagementService;
import com.b2b.b2b.modules.auth.util.UserUtils;
import com.b2b.b2b.modules.crm.deal.repository.DealRepository;
import com.b2b.b2b.modules.crm.lead.repository.LeadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserManagementServiceImpl implements UserManagementService {

    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final UserOrganizationRepository userOrgRepository;
    private final UserUtils userUtils;
    private final InvitationRepository invitationRepository;
    private final LeadRepository leadRepository;
    private final DealRepository dealRepository;

    @Override
    @Transactional
    public MemberResponseDTO acceptInvitation(AcceptInviteRequestDTO request) {
        Invitation invitation = invitationRepository.findByToken(request.getToken())
                .orElseThrow(() -> new ResourceNotFoundException("Invitation", "token", request.getToken()));
        if(invitation.isAccepted() || invitation.getExpiryDate().isBefore(LocalDateTime.now())) throw new BadResuestException("Invitation is invalid or expired.");

        User newUser = new  User(invitation.getEmail(), passwordEncoder.encode(request.getPassword()), request.getUsername());
        newUser.setUserActive(true);
        newUser.setEmailVerified(true);
        User savedUser = userRepository.save(newUser);

        boolean isFirstOrg = !userOrgRepository.existsByUser(savedUser);
        UserOrganization userOrganization = new UserOrganization(savedUser, invitation.getOrganization(), invitation.getRole(), false);
        userOrganization.setDefaultHome(isFirstOrg);
        userOrgRepository.save(userOrganization);

        invitation.setAccepted(true);
        invitationRepository.save(invitation);
        return userUtils.createMemberResponseDTO(savedUser, invitation.getRole());
    }

    @Override
    @Transactional
    public MessageResponse inviteMember(InviteMemberRequestDTO request, Integer adminOrgId) {
        String userEmail = request.getEmail();
        if(userRepository.existsByEmail(userEmail)) throw new BadResuestException("User with this email already exists.");

        Organization organization = organizationRepository.findById(adminOrgId).
                orElseThrow(() -> new ResourceNotFoundException("Organization", "id", adminOrgId));
        Role role = roleRepository.findByAppRoles(request.getRole())
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", adminOrgId));
        String token = UUID.randomUUID().toString();

        Invitation invitation = new Invitation(role, organization, token, userEmail);
        invitationRepository.save(invitation);
        //trigger email
        //emailService.sendInvitationEmail(invitation.getEmail(), token);
        log.info("email is send to invite user: {}", invitation.getEmail());
        return new MessageResponse("Invitation sent successfully to " + request.getEmail());
    }

    @Override
    public List<MemberResponseDTO> getMembersByOrganizationId(Integer orgId) {
        List<UserOrganization> userOrgs = userOrgRepository.findByOrganization_OrganizationId(orgId);
        return userOrgs.stream()
                .map(userOrg -> userUtils.createMemberResponseDTO(
                        userOrg.getUser(),
                        userOrg.getRole()
                )).toList();
    }

    @Override
    public MemberResponseDTO getMemberByUserId(Integer userId, Integer orgId) {
        UserOrganization user = userOrgRepository.findByUser_UserIdAndOrganization_OrganizationId(userId, orgId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
        return userUtils.createMemberResponseDTO(user.getUser(), user.getRole());
    }

    @Override
    @Transactional
    public void updateRole(Integer userId, AppRoles newRole, Integer orgId) {
        UserOrganization user = userOrgRepository.findByUser_UserIdAndOrganization_OrganizationId(userId, orgId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
        Role role = roleRepository.findByAppRoles(newRole)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "app Role", newRole.name()));

        user.setRole(role);
        userOrgRepository.save(user);

    }

    @Override
    @Transactional
    public void deactivateAndReassign(Integer userId, Integer successorId, Integer orgId) {
        UserOrganization userOrg = userOrgRepository.findByUser_UserIdAndOrganization_OrganizationId(userId, orgId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
        boolean successor = userOrgRepository.existsByUser_UserIdAndOrganization_OrganizationId(successorId, orgId);

        if(userOrg.isAccountOwner()) throw new BadResuestException("Cannot deactivate the Account Owner. Transfer ownership first.");
        if(!successor) throw new BadResuestException("Successor must belong to the same organization.");

        leadRepository.reassignLeads(userId,successorId, orgId);
        dealRepository.reassignDeals(userId, successorId, orgId);

        User user = userOrg.getUser();
        user.setUserActive(false);
        userRepository.save(user);

    }

    @Override
    @Transactional
    public void transferOwnerShip(Integer newOwnerId, Integer currentOwnerId, Integer orgId) {
        UserOrganization currentOwner = userOrgRepository.findByUser_UserIdAndOrganization_OrganizationId(currentOwnerId, orgId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", currentOwnerId));

        if(!currentOwner.isAccountOwner()) throw new UnauthorizedException("Only the current Account Owner can transfer ownership.");

        UserOrganization newOwner = userOrgRepository.findByUser_UserIdAndOrganization_OrganizationId(newOwnerId, orgId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", newOwnerId));

        currentOwner.setAccountOwner(false);
        newOwner.setAccountOwner(true);
        userOrgRepository.saveAll(List.of(currentOwner, newOwner));

    }


}
