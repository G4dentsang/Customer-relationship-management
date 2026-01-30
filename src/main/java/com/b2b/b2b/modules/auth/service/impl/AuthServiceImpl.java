package com.b2b.b2b.modules.auth.service.impl;

import com.b2b.b2b.exception.ResourceAlreadyExistsException;
import com.b2b.b2b.exception.ResourceNotFoundException;
import com.b2b.b2b.modules.auth.entity.*;
import com.b2b.b2b.modules.auth.repository.*;
import com.b2b.b2b.modules.auth.security.request.SignUpRequestDTO;
import com.b2b.b2b.modules.auth.service.AuthService;
import com.b2b.b2b.modules.auth.service.EmailService;
import com.b2b.b2b.modules.crm.pipeline.entity.Pipeline;
import com.b2b.b2b.modules.crm.pipeline.entity.PipelineType;
import com.b2b.b2b.modules.crm.pipeline.repository.PipelineRepository;
import com.b2b.b2b.modules.crm.pipelineStage.entity.PipelineStage;
import com.b2b.b2b.modules.crm.pipelineStage.repository.PipelineStageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService
{
     private final OrganizationRepository organizationRepository;
     private final UserRepository userRepository;
     private final PasswordEncoder passwordEncoder;
     private final RoleRepository roleRepository;
     private final UserOrganizationRepository userOrganizationRepository;
     private final EmailService emailService;
     private final PipelineRepository pipelineRepository;
     private final PipelineStageRepository pipelineStageRepository;

    @Override
    @Transactional
    public void registerOrganizationAndAdmin(SignUpRequestDTO request) {
        if(organizationRepository.existsByOrganizationName(request.getOrganizationName())) {
            throw new ResourceAlreadyExistsException("Organization name", request.getOrganizationName());
        }

        User adminUser = userRepository.findByEmail(request.getEmail())
                 .orElseGet(() -> userRepository.save(new User(
                         request.getUserName(),
                         request.getEmail(),
                         passwordEncoder.encode(request.getPassword()))
                 ));

        Organization organization = new Organization();
        organization.setOrganizationName(request.getOrganizationName());
        organizationRepository.save(organization);

        createDefaultOrganizationPipelinesWithStages(organization);
        linkUserToOrganization(adminUser, organization);

        emailService.sendVerificationEmail(adminUser);
    }

    private void createDefaultOrganizationPipelinesWithStages(Organization organization) {
        Pipeline leadPipeline = new Pipeline("Standard Lead pipeline", true, LocalDateTime.now(), PipelineType.LEAD, organization);
        pipelineRepository.save(leadPipeline);

        String[] leadStages = {"NEW", "CONTACTED", "IN_PROGRESS", "QUALIFIED", "UNQUALIFIED", "CONVERTED"};
        List<PipelineStage> leadPipelineStages = new ArrayList<>();
        for (int i = 0; i < leadStages.length; i++) {
            leadPipelineStages.add(new PipelineStage(leadStages[i], "lead pipeline stage : " + leadStages[i], i, LocalDateTime.now(), leadPipeline));
        }
        pipelineStageRepository.saveAll(leadPipelineStages);

        Pipeline dealPipeline = new Pipeline("Standard pipeline", true, LocalDateTime.now(), PipelineType.DEAL, organization);
        pipelineRepository.save(dealPipeline);

        PipelineStage pipelineDealStage = new PipelineStage("CREATED", "Default deal stage", 0, LocalDateTime.now(), dealPipeline);
        pipelineStageRepository.save(pipelineDealStage);
    }

    private void linkUserToOrganization(User adminUser, Organization organization) {
        Role adminRole = roleRepository.findByAppRoles(AppRoles.ROLE_ADMIN)
                .orElseThrow(()-> new ResourceNotFoundException("Role", "admin", AppRoles.ROLE_ADMIN.ordinal()));

        //Check if the user ALREADY has a default home assigned
        boolean hasDefaultHome = userOrganizationRepository.existsByUserAndIsDefaultHomeTrue(adminUser);

        UserOrganization userOrganization = new UserOrganization(adminUser, organization, adminRole,true);
        //If they don't have a default home yet, this new one becomes it
        userOrganization.setDefaultHome(!hasDefaultHome);

        userOrganizationRepository.save(userOrganization);
    }


}
