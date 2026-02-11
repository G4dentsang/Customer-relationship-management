package com.b2b.b2b.modules.crm.deal.internal.utils;

import com.b2b.b2b.modules.crm.deal.internal.infrastructure.persistence.Deal;
import com.b2b.b2b.modules.crm.deal.internal.infrastructure.web.dto.DealFilterDTO;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Component
public class DealSpecifications {
    public static Specification<Deal> createSearch(DealFilterDTO filter ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getSearchText() != null && !filter.getSearchText().isEmpty()) {
                String likePattern = "%" + filter.getSearchText().toLowerCase() + "%";
                Predicate name = criteriaBuilder.like(criteriaBuilder.lower(root.get("dealName")), likePattern);

                predicates.add(criteriaBuilder.or(name));
            }

            if(filter.getDealStatus() != null){
                predicates.add(criteriaBuilder.equal(root.get("dealStatus"), filter.getDealStatus()));
            }

            if(filter.getPipelineId() != null){
                predicates.add(criteriaBuilder.equal(root.get("pipeline").get("id"), filter.getPipelineId()));
            }
            if(filter.getPipelineStageId() != null){
                predicates.add(criteriaBuilder.equal(root.get("pipelineStage").get("id"), filter.getPipelineStageId()));
            }

            if(filter.getMaxAmount() != null){
                predicates.add(criteriaBuilder.equal(root.get("dealAmount"), filter.getMaxAmount()));
            }
            if(filter.getMinAmount() != null){
                predicates.add(criteriaBuilder.equal(root.get("dealAmount"), filter.getMinAmount()));
            }

            if(filter.getCreatedFrom() != null){
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), filter.getCreatedFrom().atStartOfDay()));
            }
            if(filter.getCreatedTo() != null){
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), filter.getCreatedTo().atTime(23,59,59)));
            }

            if(filter.getClosedFrom() != null){
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("closedAt"), filter.getClosedFrom().atStartOfDay()));
            }
            if(filter.getClosedTo() != null){
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("closedAt"), filter.getClosedTo().atTime(23,59,59)));
            }

            if(filter.getOwnerId() != null){
                predicates.add(criteriaBuilder.equal(root.get("assignedUser").get("id"), filter.getOwnerId()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        };
    }
}
