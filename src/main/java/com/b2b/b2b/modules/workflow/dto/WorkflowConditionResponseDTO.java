package com.b2b.b2b.modules.workflow.dto;


public record WorkflowConditionResponseDTO(
        String field,
        String workflowConditionOperator,
        String expectedValue
) { }
