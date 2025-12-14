package com.b2b.b2b.modules.workflow.dto;

import com.b2b.b2b.modules.workflow.enums.WorkflowActionType;

public record WorkflowActionResponseDTO(
       String workflowActionType,
       String actionConfigJson
) {}
