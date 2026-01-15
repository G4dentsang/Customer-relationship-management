package com.b2b.b2b.modules.workflow.service;

import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.workflow.payloads.WorkflowRuleCreateDTO;
import com.b2b.b2b.modules.workflow.payloads.WorkflowRuleResponseDTO;
import com.b2b.b2b.modules.workflow.entity.WorkflowRule;
import com.b2b.b2b.modules.workflow.enums.WorkflowTriggerType;
import com.b2b.b2b.modules.workflow.payloads.WorkflowRuleUpdateDTO;
import com.b2b.b2b.modules.workflow.payloads.WorkflowRuleUpdateMetaDataDTO;

import java.util.List;

public interface WorkflowRuleService {
    List<WorkflowRule> getActiveWorkflowRules(Organization org, WorkflowTriggerType type, Boolean isActive);
    List<WorkflowRule> getAllRulesByTriggerType(Organization org, WorkflowTriggerType type);
    List<WorkflowRuleResponseDTO> getAllWorkflowRules(User user);
    WorkflowRuleResponseDTO getWorkflowRule(Integer id, User user);
    WorkflowRuleResponseDTO create(WorkflowRuleCreateDTO request, User user);
    WorkflowRuleResponseDTO toggleStatus(Integer id, User user, Boolean status);
    WorkflowRuleResponseDTO updateMetaData(Integer id, WorkflowRuleUpdateMetaDataDTO request, User user);
    WorkflowRuleResponseDTO updateLogic(Integer id, WorkflowRuleUpdateDTO request, User user);
    void delete(Integer id, User user);
}
