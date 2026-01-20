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
import com.b2b.b2b.shared.multitenancy.OrganizationContext;
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
    public MessageResponse inviteMember(InviteMemberRequestDTO request) {
        Integer adminOrgId = OrganizationContext.getOrgId();
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
    @Transactional
    public MemberResponseDTO acceptInvitation(AcceptInviteRequestDTO request) {
        Invitation invitation = invitationRepository.findByToken(request.getToken()).orElseThrow(() -> new ResourceNotFoundException("Invitation", "token", request.getToken()));
        if (invitation.isAccepted() || invitation.getExpiryDate().isBefore(LocalDateTime.now()))
            throw new BadResuestException("Invitation is invalid or expired.");

        User newUser = new User(invitation.getEmail(), passwordEncoder.encode(request.getPassword()), request.getUsername());
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
    public List<MemberResponseDTO> getMembersByOrganization() {
        List<UserOrganization> mappings = userOrgRepository.findAll();
        return mappings.stream()
                .map(userOrg -> userUtils.createMemberResponseDTO(
                        userOrg.getUser(),
                        userOrg.getRole()
                )).toList();
    }

    @Override
    public MemberResponseDTO getMemberByUserId(Integer userId) {
        UserOrganization member = userOrgRepository.findByUser_UserId(userId).orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
        return userUtils.createMemberResponseDTO(member.getUser(), member.getRole());
    }

    @Override
    @Transactional
    public void updateRole(Integer userId, AppRoles newRole) {
        UserOrganization user = userOrgRepository.findByUser_UserId(userId).orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
        Role role = roleRepository.findByAppRoles(newRole).orElseThrow(() -> new ResourceNotFoundException("Role", "app Role", newRole.name()));

        user.setRole(role);
        userOrgRepository.save(user);

    }

    @Override
    @Transactional
    public void deactivateAndReassign(Integer userId, Integer successorId) {
        UserOrganization userOrg = userOrgRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
        boolean successor = userOrgRepository.existsByUser_UserId(successorId);

        if (userOrg.isAccountOwner())
            throw new BadResuestException("Cannot deactivate the Account Owner. Transfer ownership first.");
        if (!successor) throw new BadResuestException("Successor must belong to the same organization.");

        leadRepository.reassignLeads(userId, successorId);
        dealRepository.reassignDeals(userId, successorId);

        User user = userOrg.getUser();
        user.setUserActive(false);
        userRepository.save(user);

    }

    @Override
    @Transactional
    public void transferOwnerShip(Integer newAccOwnerId) {
        Integer orgId = OrganizationContext.getOrgId();
        UserOrganization currentAccOwner = userOrgRepository.findByIsAccountOwnerTrue().orElseThrow(() -> new ResourceNotFoundException("AccountOwner", "orgId", orgId));

        if (!currentAccOwner.isAccountOwner())
            throw new UnauthorizedException("Only the current Account Owner can transfer ownership.");

        UserOrganization newAccOwner = userOrgRepository.findByUser_UserId(newAccOwnerId).orElseThrow(() -> new ResourceNotFoundException("NewAccOwner", "userId", newAccOwnerId));

        currentAccOwner.setAccountOwner(false);
        newAccOwner.setAccountOwner(true);
        userOrgRepository.saveAll(List.of(currentAccOwner, newAccOwner));

    }


}
