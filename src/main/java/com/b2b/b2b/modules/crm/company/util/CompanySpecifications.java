package com.b2b.b2b.modules.crm.company.util;

import com.b2b.b2b.modules.crm.company.entity.Company;
import com.b2b.b2b.modules.crm.company.payloads.CompanyFilterDTO;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CompanySpecifications {
    public static Specification<Company> createSearch(CompanyFilterDTO filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getSearchText() != null && !filter.getSearchText().isEmpty()) {
                String likePattern = "%" + filter.getSearchText().toLowerCase() + "%";
                Predicate name = criteriaBuilder.like(criteriaBuilder.lower(root.get("companyName")), likePattern);
                Predicate website = criteriaBuilder.like(criteriaBuilder.lower(root.get("website")), likePattern);

                predicates.add(criteriaBuilder.or(name, website));
            }

            if (filter.getIndustry() != null && !filter.getIndustry().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("industry"), filter.getIndustry()));
            }

            if (filter.getCity() != null && !filter.getCity().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("city"), filter.getCity()));
            }
            if (filter.getState() != null && !filter.getState().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("state"), filter.getState()));
            }
            if (filter.getCountry() != null && !filter.getCountry().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("country"), filter.getCountry()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        };
    }
}
