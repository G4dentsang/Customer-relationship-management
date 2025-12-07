package com.b2b.b2b.modules.auth.service;

import com.b2b.b2b.modules.auth.entity.*;
import com.b2b.b2b.modules.auth.repository.*;
import com.b2b.b2b.modules.auth.security.request.SignUpRequestDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class AuthServiceImpl implements AuthService
{
     private final OrganizationRepository organizationRepository;
     private final UserRepository userRepository;
     private final PasswordEncoder passwordEncoder;
     private final RoleRepository roleRepository;
     private final UserOrganizaitonRepository userOrganizaitonRepository;
     private final EmailVerificationService emailVerificationService;


    public AuthServiceImpl(OrganizationRepository organizationRepository, UserRepository userRepository, PasswordEncoder passwordEncoder,
                           RoleRepository roleRepository, UserOrganizaitonRepository userOrganizaitonRepository,EmailVerificationService emailVerificationService)
     {
         this.organizationRepository = organizationRepository;
         this.userRepository = userRepository;
         this.passwordEncoder = passwordEncoder;
         this.roleRepository = roleRepository;
         this.userOrganizaitonRepository = userOrganizaitonRepository;
         this.emailVerificationService = emailVerificationService;
     }
    @Override
    public void register(SignUpRequestDTO signUpRequestDTO) {
        //create organization
        Organization organization = new Organization(signUpRequestDTO.getOrganizationName(), LocalDate.now());
        organizationRepository.save(organization);

        //create user
        User user  = new User(signUpRequestDTO.getUserName(),signUpRequestDTO.getEmail(), passwordEncoder.encode(signUpRequestDTO.getPassword()));
        userRepository.save(user);
        Role adminRole = roleRepository.findByAppRoles(AppRoles.ROLE_ADMIN).orElseThrow(()-> new RuntimeException("ROLE_ADMIN not found"));

        //link User + Organization + Role
        UserOrganization userOrganization = new UserOrganization(user,organization,adminRole,true);
        userOrganizaitonRepository.save(userOrganization);
        emailVerificationService.sendVerificationEmail(user);

    }
}
