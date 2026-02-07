package com.b2b.b2b.modules.workflow.defination.service;

import com.b2b.b2b.modules.organization.model.Organization;
import com.b2b.b2b.modules.workflow.defination.payloads.WorkflowRuleCreateDTO;
import com.b2b.b2b.modules.workflow.defination.payloads.WorkflowRuleResponseDTO;
import com.b2b.b2b.modules.workflow.defination.model.WorkflowRule;
import com.b2b.b2b.modules.workflow.enums.WorkflowTriggerType;
import com.b2b.b2b.modules.workflow.defination.payloads.WorkflowRuleUpdateDTO;
import com.b2b.b2b.modules.workflow.defination.payloads.WorkflowRuleUpdateMetaDataDTO;
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
