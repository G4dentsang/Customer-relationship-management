package com.b2b.b2b.modules.workflow.service;

import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.workflow.payloads.WorkflowRuleCreateDTO;
import com.b2b.b2b.modules.workflow.payloads.WorkflowRuleResponseDTO;
import com.b2b.b2b.modules.workflow.entity.WorkflowRule;
import com.b2b.b2b.modules.workflow.enums.WorkflowTriggerType;
import com.b2b.b2b.modules.workflow.payloads.WorkflowRuleUpdateDTO;
import com.b2b.b2b.modules.workflow.payloads.WorkflowRuleUpdateMetaDataDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WorkflowRuleService {
    List<WorkflowRule> getActiveWorkflowRules(Organization org, WorkflowTriggerType type, Boolean isActive);
    List<WorkflowRule> getAllRulesByTriggerType(Organization org, WorkflowTriggerType type);
    Page<WorkflowRuleResponseDTO> getAllWorkflowRules(Pageable pageable);
    WorkflowRuleResponseDTO getWorkflowRule(Integer id);
    WorkflowRuleResponseDTO create(WorkflowRuleCreateDTO request);
    WorkflowRuleResponseDTO toggleStatus(Integer id, Boolean status);
    WorkflowRuleResponseDTO updateMetaData(Integer id, WorkflowRuleUpdateMetaDataDTO request);
    WorkflowRuleResponseDTO updateLogic(Integer id, WorkflowRuleUpdateDTO request);
    void delete(Integer id);
}
