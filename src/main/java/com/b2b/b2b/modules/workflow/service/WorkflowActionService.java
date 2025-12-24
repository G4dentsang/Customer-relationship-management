package com.b2b.b2b.modules.workflow.service;

import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.workflow.entity.WorkflowAction;
import com.b2b.b2b.modules.workflow.payloads.WorkflowActionDTO;
import com.b2b.b2b.modules.workflow.payloads.WorkflowActionResponseDTO;

import java.util.List;

public interface WorkflowActionService {
    void execute(WorkflowAction action, WorkflowTarget target);
    void executeEmailAction(WorkflowAction action, WorkflowTarget target);
    void executeAssignmentAction(WorkflowAction action, WorkflowTarget target);
    List<WorkflowActionResponseDTO> addWorkflowActions(Integer ruleId, List<WorkflowActionDTO> workflowActionDTOs, User user);
}
