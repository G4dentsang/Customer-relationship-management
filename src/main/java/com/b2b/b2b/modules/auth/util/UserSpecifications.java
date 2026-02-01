package com.b2b.b2b.modules.auth.util;

import com.b2b.b2b.modules.auth.entity.UserOrganization;
import com.b2b.b2b.modules.auth.payloads.UserFilterDTO;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserSpecifications {
    public static Specification<UserOrganization> createSearch(UserFilterDTO filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filter.getSearchText() != null && !filter.getSearchText().isEmpty()) {
                String likePattern = "%" + filter.getSearchText().toLowerCase() + "%";
                Predicate name = criteriaBuilder.like(root.get("userName"), likePattern);
                Predicate email = criteriaBuilder.like(root.get("email"), likePattern);
                predicates.add(criteriaBuilder.or(name, email));
            }

            if (filter.getUserActive() != null) {
                predicates.add(criteriaBuilder.equal(root.get("userActive"), filter.getUserActive()));
            }

            if (filter.getEmailVarified() != null) {
                predicates.add(criteriaBuilder.equal(root.get("emailVarified"), filter.getEmailVarified()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
