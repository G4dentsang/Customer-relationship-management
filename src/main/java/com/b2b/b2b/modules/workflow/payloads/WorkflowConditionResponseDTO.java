package com.b2b.b2b.modules.workflow.payloads;


import com.b2b.b2b.modules.workflow.enums.WorkflowConditionOperator;

public record WorkflowConditionResponseDTO(
        Integer id,
        String field,
        WorkflowConditionOperator workflowConditionOperator,
        String expectedValue
) { }
