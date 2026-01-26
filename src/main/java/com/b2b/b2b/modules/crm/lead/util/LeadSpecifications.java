package com.b2b.b2b.modules.crm.lead.util;

import com.b2b.b2b.modules.crm.lead.entity.Lead;
import com.b2b.b2b.modules.crm.lead.payloads.LeadFilterDTO;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class LeadSpecifications {

    public static Specification<Lead> createSearch(LeadFilterDTO filter ){
        return(root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(filter.getSearchText() != null && !filter.getSearchText().isEmpty()){
                String likePattern = "%" + filter.getSearchText().toLowerCase() + "%";
                Predicate name = criteriaBuilder.like(criteriaBuilder.lower(root.get("leadName")), likePattern);
                Predicate email = criteriaBuilder.like(criteriaBuilder.lower(root.get("leadEmail")), likePattern);
                Predicate phone = criteriaBuilder.like(criteriaBuilder.lower(root.get("leadPhone")), likePattern);

                predicates.add(criteriaBuilder.or(name, email, phone));
            }

            if(filter.getLeadStatus() != null){
                predicates.add(criteriaBuilder.equal(root.get("leadStatus"), filter.getLeadStatus()));
            }

            if(filter.getPipelineStageId() != null){
                predicates.add(criteriaBuilder.equal(root.get("pipelineStage").get("id"), filter.getPipelineStageId()));
            }

            if(filter.getIsConverted() != null){
                predicates.add(criteriaBuilder.equal(root.get("isConverted"), filter.getIsConverted()));
            }

            if(filter.getStartDate() != null){
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), filter.getStartDate().atStartOfDay()));
            }
            if(filter.getEndDate() != null){
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), filter.getEndDate().atTime(23,59,59)));
            }

            if(filter.getOwnerId() != null){
                predicates.add(criteriaBuilder.equal(root.get("assignedUser").get("id"), filter.getOwnerId()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        };

    }
}
