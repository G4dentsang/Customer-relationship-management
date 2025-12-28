package com.b2b.b2b.modules.workflow.actions;

import com.b2b.b2b.modules.workflow.entity.WorkflowAction;
import com.b2b.b2b.modules.workflow.enums.WorkflowActionType;
import com.b2b.b2b.modules.workflow.service.WorkflowTarget;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AssignmentAction implements WorkflowActionHandler{
    @Override
    public WorkflowActionType getType() {
        return  WorkflowActionType.ASSIGN_USER;
    }

    @Override
    public void handle(WorkflowAction action, WorkflowTarget target) {
      log.info("Assigning user via workflow...");
    }
}
