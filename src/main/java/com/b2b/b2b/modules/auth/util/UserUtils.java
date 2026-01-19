package com.b2b.b2b.modules.auth.util;

import com.b2b.b2b.modules.auth.entity.Role;
import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.auth.payloads.MemberResponseDTO;
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
