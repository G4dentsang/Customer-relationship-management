package com.b2b.b2b.modules.workflow.service;

import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.workflow.payloads.WorkflowRuleCreateDTO;
import com.b2b.b2b.modules.workflow.payloads.WorkflowRuleResponseDTO;
import com.b2b.b2b.modules.workflow.entity.WorkflowRule;
import com.b2b.b2b.modules.workflow.enums.WorkflowTriggerType;

import java.util.List;

public interface WorkflowRuleService {
    List<WorkflowRule> getWorkflowRules(Organization org, WorkflowTriggerType type, Boolean isActive);
    List<WorkflowRuleResponseDTO> getAllWorkflowRules(User user);
    WorkflowRuleResponseDTO getWorkflowRule(Integer id, User user);
    WorkflowRuleResponseDTO create(WorkflowRuleCreateDTO request, User user);
    WorkflowRuleResponseDTO updateStatus(Integer id, User user, Boolean status);
}
