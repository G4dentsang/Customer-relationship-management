package com.b2b.b2b.modules.crm.pipelineStage.util;

import com.b2b.b2b.modules.crm.pipelineStage.entity.PipelineStage;
import com.b2b.b2b.modules.crm.pipelineStage.payloads.PipelineStageFilterDTO;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
/***********************Will see later***********************/
@Component
public class PipelineStageSpecifications {
    public static Specification<PipelineStage> createSearch(PipelineStageFilterDTO filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getSearchText() != null && !filter.getSearchText().isEmpty()) {
                String likePattern = "%" + filter.getSearchText().toLowerCase() + "%";
                Predicate name = criteriaBuilder.like(criteriaBuilder.lower(root.get("stageName")), likePattern);
                predicates.add(criteriaBuilder.or(name));
            }
            if (filter.getPipelineId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("pipelineId"), filter.getPipelineId()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        };
    }
}
