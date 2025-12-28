package com.b2b.b2b.modules.workflow.actions;

import com.b2b.b2b.modules.workflow.entity.WorkflowAction;
import com.b2b.b2b.modules.workflow.enums.WorkflowActionType;
import com.b2b.b2b.modules.workflow.service.WorkflowTarget;

public interface WorkflowActionHandler {
    WorkflowActionType getType();
    void handle(WorkflowAction action, WorkflowTarget target);
}
