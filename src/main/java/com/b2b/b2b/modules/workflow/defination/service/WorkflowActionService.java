package com.b2b.b2b.modules.workflow.defination.service;

import com.b2b.b2b.modules.workflow.defination.model.WorkflowAction;
import com.b2b.b2b.modules.workflow.defination.payloads.WorkflowActionDTO;
import com.b2b.b2b.modules.workflow.defination.payloads.WorkflowActionResponseDTO;

import java.util.List;

public interface WorkflowActionService {
    void execute(WorkflowAction action, WorkflowTarget target);
    List<WorkflowActionResponseDTO> addActions(Integer ruleId, List<WorkflowActionDTO> actions);
    void deleteAction(Integer ruleId, Long actionId);
}
