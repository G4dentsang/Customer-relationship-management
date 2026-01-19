package com.b2b.b2b.modules.auth.service.impl;

import com.b2b.b2b.modules.auth.entity.*;
import com.b2b.b2b.modules.auth.repository.*;
import com.b2b.b2b.modules.auth.security.request.SignUpRequestDTO;
import com.b2b.b2b.modules.auth.service.AuthService;
import com.b2b.b2b.modules.auth.service.EmailVerificationService;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

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
     private final EmailVerificationService emailVerificationService;
     private final PipelineRepository pipelineRepository;
     private final PipelineStageRepository pipelineStageRepository;

    @Override
    @Transactional
    public void register(SignUpRequestDTO signUpRequestDTO) {
       Optional<User> existingUser = userRepository.existByEmail(signUpRequestDTO.getEmail());
        User user;
        if(existingUser.isPresent()){
            user = existingUser.get();
        }else{
            user = new User(signUpRequestDTO.getUserName(),signUpRequestDTO.getEmail(), passwordEncoder.encode(signUpRequestDTO.getPassword()));
            user =  userRepository.save(user);
        }
        //create organization
        Organization organization = new Organization(signUpRequestDTO.getOrganizationName(), LocalDate.now());
        organizationRepository.save(organization);
        //create default pipeline with default pipeline stage for all organization
        Pipeline  defaultOrganizationLeadPipeline = new Pipeline("Standard pipeline", true, LocalDateTime.now(), PipelineType.LEAD, organization);
        pipelineRepository.save(defaultOrganizationLeadPipeline);

        String[] leadPipelineStages = {"NEW","CONTACTED", "IN_PROGRESS", "QUALIFIED", "UNQUALIFIED", "CONVERTED"};
        for (int i = 0; i < leadPipelineStages.length; i++) {
           pipelineStageRepository.save(new PipelineStage( leadPipelineStages[i], "lead pipeline stage : " + leadPipelineStages[i],i, LocalDateTime.now(), defaultOrganizationLeadPipeline));
        }
        //for deal also later during dealUpdate method
        Pipeline  defaultOrganizationDealPipeline = new Pipeline("Standard pipeline", true, LocalDateTime.now(),PipelineType.DEAL, organization);
        PipelineStage pipelineDealStage = new PipelineStage("CREATED", "this is a default deal pipeline stage",0, LocalDateTime.now(), defaultOrganizationDealPipeline);
        pipelineRepository.save(defaultOrganizationDealPipeline);
        pipelineStageRepository.save(pipelineDealStage);

        Role adminRole = roleRepository.findByAppRoles(AppRoles.ROLE_ADMIN).orElseThrow(()-> new RuntimeException("ROLE_ADMIN not found"));

        //link User + Organization + Role
        boolean isFirstOrg = !userOrganizationRepository.existsByUser(user);
        UserOrganization userOrganization = new UserOrganization(user,organization,adminRole,true);
        userOrganization.setDefaultHome(isFirstOrg);
        userOrganizationRepository.save(userOrganization);
      //  emailVerificationService.sendVerificationEmail(user);

    }
}
