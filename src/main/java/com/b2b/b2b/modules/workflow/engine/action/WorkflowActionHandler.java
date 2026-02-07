package com.b2b.b2b.modules.workflow.engine.action;

import com.b2b.b2b.modules.workflow.defination.model.WorkflowAction;
import com.b2b.b2b.modules.workflow.enums.WorkflowActionType;
import com.b2b.b2b.modules.workflow.defination.service.WorkflowTarget;

public interface WorkflowActionHandler {
    WorkflowActionType getType();
    void handle(WorkflowAction action, WorkflowTarget target);
}
