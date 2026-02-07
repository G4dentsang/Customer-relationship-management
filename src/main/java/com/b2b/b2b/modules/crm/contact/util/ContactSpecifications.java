package com.b2b.b2b.modules.crm.contact.util;

import com.b2b.b2b.modules.crm.contact.model.Contact;
import com.b2b.b2b.modules.crm.contact.payloads.ContactFilterDTO;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ContactSpecifications {
    public static Specification<Contact> createSearch(ContactFilterDTO filter){
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getSearchText() != null && !filter.getSearchText().isEmpty()) {
                String likePattern = "%" + filter.getSearchText().toLowerCase() + "%";
                Predicate fName = criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), likePattern);
                Predicate lName = criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), likePattern);
                Predicate email = criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), likePattern);
                Predicate phone = criteriaBuilder.like(criteriaBuilder.lower(root.get("phone")), likePattern);

                predicates.add(criteriaBuilder.or(fName, lName, email, phone));
            }

            if (filter.getCompanyId() != null){
                predicates.add(criteriaBuilder.equal(root.get("company"), filter.getCompanyId()));
            }

            if(filter.getJobTitle() != null){
                predicates.add(criteriaBuilder.equal(root.get("jobTitle"), filter.getJobTitle()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
