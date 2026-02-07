package com.b2b.b2b.modules.organization.util;

import com.b2b.b2b.modules.organization.model.Role;
import com.b2b.b2b.modules.user.model.User;
import com.b2b.b2b.modules.organization.payload.MemberResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class UserUtils {
    public MemberResponseDTO createMemberResponseDTO(User user, Role role){
       return new MemberResponseDTO(
               user.getUserId(),
               user.getUserName(),
               user.getEmail(),
               role.getAppRoles().name(),
               user.isUserActive(),
               user.getCreatedAt()

       );
    }
}
