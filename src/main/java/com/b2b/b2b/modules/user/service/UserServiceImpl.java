package com.b2b.b2b.modules.user.service;

import com.b2b.b2b.exception.BadRequestException;
import com.b2b.b2b.exception.ResourceNotFoundException;
import com.b2b.b2b.exception.UnauthorizedException;
import com.b2b.b2b.modules.auth.payload.*;
import com.b2b.b2b.modules.auth.security.services.UserDetailImpl;
import com.b2b.b2b.modules.notification.service.EmailService;
import com.b2b.b2b.modules.user.util.UserSpecifications;
import com.b2b.b2b.modules.organization.util.UserUtils;
import com.b2b.b2b.modules.crm.deal.persistence.DealRepository;
import com.b2b.b2b.modules.crm.lead.persistence.LeadRepository;
import com.b2b.b2b.modules.organization.model.*;
import com.b2b.b2b.modules.organization.payload.AcceptInviteRequestDTO;
import com.b2b.b2b.modules.organization.payload.InviteMemberRequestDTO;
import com.b2b.b2b.modules.organization.payload.MemberResponseDTO;
import com.b2b.b2b.modules.organization.persistence.InvitationRepository;
import com.b2b.b2b.modules.organization.persistence.OrganizationRepository;
import com.b2b.b2b.modules.organization.persistence.RoleRepository;
import com.b2b.b2b.modules.organization.persistence.UserOrganizationRepository;
import com.b2b.b2b.modules.user.model.User;
import com.b2b.b2b.modules.user.payload.UserFilterDTO;
import com.b2b.b2b.modules.user.persistence.UserRepository;
import com.b2b.b2b.shared.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final UserOrganizationRepository userOrgRepository;
    private final UserUtils userUtils;
    private final InvitationRepository invitationRepository;
    private final LeadRepository leadRepository;
    private final DealRepository dealRepository;
    private final EmailService emailService;
    private final AuthUtil authUtil;

    @Override
    @Transactional
    public MessageResponse inviteMember(InviteMemberRequestDTO request) {
        UserDetailImpl userDetail = (UserDetailImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer orgId = userDetail.getOrganizationId();
        Organization organization = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", orgId));
        Role role = roleRepository.findByAppRoles(request.getRole())
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", orgId));
        String token = UUID.randomUUID().toString();

        Invitation invitation = new Invitation(role, organization, token, request.getEmail());
        invitationRepository.save(invitation);
        emailService.sendInvitationEmail(invitation);
        log.info("email is send to invite user: {}", invitation.getEmail());
        return new MessageResponse("Invitation sent successfully to " + request.getEmail());
    }

    @Override
    @Transactional
    public MemberResponseDTO acceptInvitation(AcceptInviteRequestDTO request) {
        Invitation invitation = invitationRepository.findByToken(request.getToken()).orElseThrow(() -> new ResourceNotFoundException("Invitation", "token", request.getToken()));
        if (invitation.isAccepted() || invitation.getExpiryDate().isBefore(LocalDateTime.now()))
            throw new BadRequestException("Invitation is invalid or expired.");

        User newUser = new User();
        newUser.setUserName(request.getUsername());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setEmail(invitation.getEmail());
        newUser.setUserActive(true);
        newUser.setEmailVerified(true);
        User savedUser = userRepository.save(newUser);

        boolean isFirstOrg = userOrgRepository.existsByUser(savedUser);
        UserOrganization userOrganization = new UserOrganization(savedUser, invitation.getOrganization(), invitation.getRole(), false);

        if(!isFirstOrg) {
            userOrganization.setDefaultHome(false);
        }
        userOrganization.setDefaultHome(true);
        userOrgRepository.save(userOrganization);

        invitation.setAccepted(true);
        invitationRepository.save(invitation);
        return userUtils.createMemberResponseDTO(savedUser, invitation.getRole());
    }

    @Override
    public Page<MemberResponseDTO> getMembersByOrganization(UserFilterDTO filter, Pageable pageable) {
        Specification<UserOrganization> spec = UserSpecifications.createSearch(filter);
        Page<UserOrganization> mappings = userOrgRepository.findAll(spec,pageable);
        return mappings.map(userOrg -> userUtils.createMemberResponseDTO(
                        userOrg.getUser(),
                        userOrg.getRole()
                ));
    }

    @Override
    public MemberResponseDTO getMemberByUserId(Integer userId) {
        UserDetailImpl userDetail = (UserDetailImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Integer orgId = userDetail.getOrganizationId();
        Organization organization = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", orgId));

        UserOrganization member = userOrgRepository.findByUser_UserIdAndOrganization(userId, organization)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        return userUtils.createMemberResponseDTO(member.getUser(), member.getRole());
    }

    @Override
    @Transactional
    public void updateRole(Integer userId, AppRoles newRole) {
        UserDetailImpl userDetail = (UserDetailImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Integer orgId = userDetail.getOrganizationId();
        Organization organization = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", orgId));

        UserOrganization userOrg = userOrgRepository.findByUser_UserIdAndOrganization(userId, organization)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
        Role role = roleRepository.findByAppRoles(newRole).orElseThrow(() -> new ResourceNotFoundException("Role", "app Role", newRole.name()));

        userOrg.setRole(role);
        userOrgRepository.save(userOrg);
    }

    @Override
    @Transactional
    public void deactivateAndReassign(Integer userId, Integer successorId) {
        UserDetailImpl userDetail = (UserDetailImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Integer orgId = userDetail.getOrganizationId();
        Organization organization = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", orgId));

        UserOrganization userOrg = userOrgRepository.findByUser_UserIdAndOrganization(userId, organization)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        boolean successor = userOrgRepository.existsByUser_UserIdAndOrganization(successorId, organization );

        if (userOrg.isAccountOwner())
            throw new BadRequestException("Cannot deactivate the Account Owner. Transfer ownership first.");
        if (!successor) throw new BadRequestException("Successor must belong to the same organization.");

        leadRepository.reassignLeads(userId, successorId);
        dealRepository.reassignDeals(userId, successorId);

        User user = userOrg.getUser();
        user.setUserActive(false);
        userRepository.save(user);

    }

    @Override
    @Transactional
    public void transferOwnerShip(Integer newAccOwnerId) {
        UserDetailImpl userDetail = (UserDetailImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Integer orgId = userDetail.getOrganizationId();
        Integer currentUserId = userDetail.getId();

        if(currentUserId.equals(newAccOwnerId)) {
            throw new IllegalArgumentException("You are already the Account Owner.");
        }

        UserOrganization currentAccOwner = userOrgRepository.findByOrganization_OrganizationIdAndUser_UserId(orgId, currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("AccountOwner", "currentUSerId", currentUserId));

        if (!currentAccOwner.isAccountOwner())
            throw new UnauthorizedException("Only the current Account Owner can transfer ownership.");

        UserOrganization newAccOwner = userOrgRepository.findByOrganization_OrganizationIdAndUser_UserId(orgId, newAccOwnerId)
                .orElseThrow(() -> new ResourceNotFoundException("NewAccOwner", "userId", newAccOwnerId));

        currentAccOwner.setAccountOwner(false);
        newAccOwner.setAccountOwner(true);
        newAccOwner.setRole(currentAccOwner.getRole());

        userOrgRepository.saveAll(List.of(currentAccOwner, newAccOwner));

        log.info("Ownership transferred from User {} to User {} for Org {}", currentUserId, newAccOwnerId, orgId);

    }


}
