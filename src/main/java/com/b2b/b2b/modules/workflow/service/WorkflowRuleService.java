package com.b2b.b2b.modules.workflow.service;

import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.workflow.dto.WorkflowRuleCreateDTO;
import com.b2b.b2b.modules.workflow.dto.WorkflowRuleResponseDTO;
import com.b2b.b2b.modules.workflow.entity.WorkflowRule;
import com.b2b.b2b.modules.workflow.enums.WorkflowTriggerType;

import java.util.List;

public interface WorkflowRuleService {
    List<WorkflowRule> getWorkflowRules(Integer orgId, WorkflowTriggerType triggerType, Boolean isActive);
    WorkflowRuleResponseDTO saveWorkflowRule(WorkflowRuleCreateDTO workflowRuleCreateDTO, User user);
}
