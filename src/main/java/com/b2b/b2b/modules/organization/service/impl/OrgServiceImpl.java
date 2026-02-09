package com.b2b.b2b.modules.organization.service.impl;

import com.b2b.b2b.exception.ResourceAlreadyExistsException;
import com.b2b.b2b.exception.ResourceNotFoundException;
import com.b2b.b2b.modules.organization.payload.RegisterOrganizationRequestDTO;
import com.b2b.b2b.modules.auth.service.AuthService;
import com.b2b.b2b.modules.crm.pipeline.model.DealPipeline;
import com.b2b.b2b.modules.crm.pipeline.model.LeadPipeline;
import com.b2b.b2b.modules.crm.pipeline.service.DealPipelineService;
import com.b2b.b2b.modules.crm.pipeline.service.LeadPipelineService;
import com.b2b.b2b.modules.crm.pipelineStage.service.DealPipelineStageService;
import com.b2b.b2b.modules.crm.pipelineStage.service.LeadPipelineStageService;
import com.b2b.b2b.modules.organization.model.AppRoles;
import com.b2b.b2b.modules.organization.model.Organization;
import com.b2b.b2b.modules.organization.model.Role;
import com.b2b.b2b.modules.organization.model.UserOrganization;
import com.b2b.b2b.modules.organization.persistence.OrganizationRepository;
import com.b2b.b2b.modules.organization.persistence.RoleRepository;
import com.b2b.b2b.modules.organization.persistence.UserOrganizationRepository;
import com.b2b.b2b.modules.organization.service.OrgService;
import com.b2b.b2b.modules.user.model.User;
import com.b2b.b2b.modules.user.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrgServiceImpl implements OrgService
{
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LeadPipelineService leadPipelineService;
    private final LeadPipelineStageService leadPipelineStageService;
    private final DealPipelineService dealPipelineService;
    private final DealPipelineStageService dealPipelineStageService;
    private final AuthService authService;
    private final UserOrganizationRepository userOrganizationRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public void registerOrganizationAndAdmin(RegisterOrganizationRequestDTO request) {
        if (organizationRepository.existsByOrganizationName(request.getOrganizationName())) {
            throw new ResourceAlreadyExistsException("Organization name", request.getOrganizationName());
        }

        User adminUser = userRepository.findByEmail(request.getEmail())
                .orElseGet(() -> userRepository.save(new User(request.getUserName(), request.getEmail(), passwordEncoder.encode(request.getPassword()))));

        Organization org = new Organization();
        org.setOrganizationName(request.getOrganizationName());
        organizationRepository.save(org);

        LeadPipeline leadPipeline = leadPipelineService.createDefaultPipeline(org);
        leadPipelineStageService.createDefaultStages(leadPipeline);

        DealPipeline dealPipeline = dealPipelineService.createDefaultPipeline(org);
        dealPipelineStageService.createDefaultStages(dealPipeline);

        linkUserToOrganization(adminUser, org);

        authService.createAndSendVerificationCode(adminUser);
    }

    private void linkUserToOrganization(User adminUser, Organization organization) {
        Role adminRole = roleRepository.findByAppRoles(AppRoles.ROLE_ADMIN)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "admin", AppRoles.ROLE_ADMIN.ordinal()));

        //Check if the user ALREADY has a default home assigned
        boolean hasDefaultHome = userOrganizationRepository.existsByUserAndIsDefaultHomeTrue(adminUser);

        UserOrganization userOrganization = new UserOrganization(adminUser, organization, adminRole, true);
        //If they don't have a default home yet, this new one becomes it
        userOrganization.setDefaultHome(!hasDefaultHome);

        userOrganizationRepository.save(userOrganization);
    }

}
