package com.b2b.b2b.modules.workflow.service;

import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.workflow.entity.WorkflowAction;
import com.b2b.b2b.modules.workflow.payloads.WorkflowActionDTO;
import com.b2b.b2b.modules.workflow.payloads.WorkflowActionResponseDTO;

import java.util.List;

public interface WorkflowActionService {
    void execute(WorkflowAction action, WorkflowTarget target);
    List<WorkflowActionResponseDTO> addActions(Integer ruleId, List<WorkflowActionDTO> actions, User user);
    void deleteAction(Integer ruleId, Long actionId, User user);
}
