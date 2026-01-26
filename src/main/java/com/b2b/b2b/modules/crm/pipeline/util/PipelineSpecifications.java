package com.b2b.b2b.modules.crm.pipeline.util;

import com.b2b.b2b.modules.crm.pipeline.entity.Pipeline;
import com.b2b.b2b.modules.crm.pipeline.payloads.PipelineFilterDTO;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PipelineSpecifications {
    public static Specification<Pipeline> createSearch(PipelineFilterDTO filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filter.getSearchText() != null && !filter.getSearchText().equals("")) {
                String likePattern = "%" + filter.getSearchText().toLowerCase() + "%";
                Predicate name = criteriaBuilder.like(criteriaBuilder.lower(root.get("pipelineName")), likePattern);
                predicates.add(criteriaBuilder.or(name));
            }
            if (filter.getPipelineType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("pipelineType"), filter.getPipelineType()));
            }
            if (filter.getIsActive()) {
                predicates.add(criteriaBuilder.equal(root.get("isActive"), filter.getIsActive()));
            }
            if (filter.getIsDefault()) {
                predicates.add(criteriaBuilder.equal(root.get("isDefault"), filter.getIsDefault()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
