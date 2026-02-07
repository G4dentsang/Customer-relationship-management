package com.b2b.b2b.modules.workflow.payloads;

import com.b2b.b2b.modules.workflow.enums.WorkflowActionType;

public record WorkflowActionResponseDTO(
        Integer id,
        WorkflowActionType workflowActionType,
        String actionConfigJson
) {
}
