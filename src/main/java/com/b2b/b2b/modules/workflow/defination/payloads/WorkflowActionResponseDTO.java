package com.b2b.b2b.modules.workflow.defination.payloads;

import com.b2b.b2b.modules.workflow.enums.WorkflowActionType;

public record WorkflowActionResponseDTO(
        Integer id,
        WorkflowActionType workflowActionType,
        String actionConfigJson
) {
}
