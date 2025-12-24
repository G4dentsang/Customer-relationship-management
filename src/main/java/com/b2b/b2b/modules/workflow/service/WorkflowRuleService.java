package com.b2b.b2b.modules.workflow.service;

import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.workflow.payloads.WorkflowRuleCreateDTO;
import com.b2b.b2b.modules.workflow.payloads.WorkflowRuleResponseDTO;
import com.b2b.b2b.modules.workflow.entity.WorkflowRule;
import com.b2b.b2b.modules.workflow.enums.WorkflowTriggerType;

import java.util.List;

public interface WorkflowRuleService {
    List<WorkflowRule> getWorkflowRules(Integer orgId, WorkflowTriggerType triggerType, Boolean isActive);
    List<WorkflowRuleResponseDTO> getAllWorkflowRules(User user);
    WorkflowRuleResponseDTO getWorkflowRule(Integer ruleId, User user);
    WorkflowRuleResponseDTO saveWorkflowRule(WorkflowRuleCreateDTO workflowRuleCreateDTO, User user);
    WorkflowRuleResponseDTO activateWorkflowRule(Integer ruleId, User user);
    WorkflowRuleResponseDTO deactivateWorkflowRule(Integer ruleId, User user);
}
