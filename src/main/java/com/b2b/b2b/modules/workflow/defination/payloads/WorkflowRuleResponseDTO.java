package com.b2b.b2b.modules.workflow.defination.payloads;

import java.time.LocalDateTime;
import java.util.List;

public record WorkflowRuleResponseDTO(
        Integer id,
        String name,
        String description,
        String workflowTriggerType,
        boolean active,
        List<WorkflowConditionResponseDTO> workflowConditions,
        List<WorkflowActionResponseDTO> workflowActions,
        LocalDateTime createdAt
) {

}
