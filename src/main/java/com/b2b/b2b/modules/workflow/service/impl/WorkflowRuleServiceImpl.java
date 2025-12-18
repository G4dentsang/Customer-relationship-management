package com.b2b.b2b.modules.workflow.service.impl;

import com.b2b.b2b.exception.APIException;
import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.workflow.dto.WorkflowActionResponseDTO;
import com.b2b.b2b.modules.workflow.dto.WorkflowConditionResponseDTO;
import com.b2b.b2b.modules.workflow.dto.WorkflowRuleCreateDTO;
import com.b2b.b2b.modules.workflow.dto.WorkflowRuleResponseDTO;
import com.b2b.b2b.modules.workflow.entity.WorkflowAction;
import com.b2b.b2b.modules.workflow.entity.WorkflowCondition;
import com.b2b.b2b.modules.workflow.entity.WorkflowRule;
import com.b2b.b2b.modules.workflow.enums.WorkflowTriggerType;
import com.b2b.b2b.modules.workflow.repository.WorkflowRuleRepository;
import com.b2b.b2b.modules.workflow.service.WorkflowRuleService;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class WorkflowRuleServiceImpl implements WorkflowRuleService {
    private final WorkflowRuleRepository workflowRuleRepository;
    public WorkflowRuleServiceImpl(WorkflowRuleRepository workflowRuleRepository) {
        this.workflowRuleRepository = workflowRuleRepository;
    }
    @Override
    public List<WorkflowRule> getWorkflowRules(Integer orgId, WorkflowTriggerType triggerType, Boolean isActive) {
        return workflowRuleRepository.findByOrganization_organizationIdAndWorkflowTriggerTypeAndIsActive(orgId, triggerType, isActive);
    }

    public List<WorkflowRule> getWorkflowRulesEAGERLY(Integer orgId, WorkflowTriggerType triggerType, Boolean isActive) {
        return workflowRuleRepository.findWorkflowRulesEagerly(orgId, triggerType, isActive);

    }

    @Override
    public WorkflowRuleResponseDTO saveWorkflowRule(WorkflowRuleCreateDTO workflowRuleCreateDTO, User user) {
        //get organization
        Organization organization = user.getUserOrganizations()
                .stream()
                .filter((userOrganization -> userOrganization.isPrimary()))
                .findFirst()
                .orElseThrow(()-> new APIException("User has no primary organization"))
                .getOrganization();
        WorkflowRule workflowRule = new WorkflowRule();
        workflowRule.setName(workflowRuleCreateDTO.getName());
        workflowRule.setDescription(workflowRuleCreateDTO.getDescription());
        workflowRule.setWorkflowTriggerType(workflowRuleCreateDTO.getWorkflowTriggerType());
        workflowRule.setActive(workflowRuleCreateDTO.isActive());
        workflowRule.setOrganization(organization);

        List<WorkflowCondition> conditions = workflowRuleCreateDTO.getWorkflowConditions()
                        .stream()
                                .map(c ->{
                                    WorkflowCondition workflowCondition = new WorkflowCondition();
                                    workflowCondition.setField(c.getField());
                                    workflowCondition.setWorkflowConditionOperator(c.getWorkflowConditionOperator());
                                    workflowCondition.setExpectedValue(c.getExpectedValue());
                                    workflowCondition.setWorkflowRule(workflowRule);
                                    return workflowCondition;
                                }).toList();
        List<WorkflowAction> actions = workflowRuleCreateDTO.getWorkflowActions()
                        .stream()
                                .map(a ->{
                                    WorkflowAction workflowAction = new WorkflowAction( );
                                    workflowAction.setActionType(a.getWorkflowActionType());
                                    workflowAction.setActionConfigJson(a.getActionConfigJson());
                                    workflowAction.setWorkflowRule(workflowRule);
                                    return workflowAction;
                                }).toList();
        workflowRule.setWorkflowConditions(conditions);
        workflowRule.setWorkflowActions(actions);
        WorkflowRule savedRule = workflowRuleRepository.save(workflowRule);

        //mapping to response DTO******************
        List<WorkflowConditionResponseDTO> workflowConditions = savedRule.getWorkflowConditions()
                .stream()
                .map(c -> new WorkflowConditionResponseDTO(
                        c.getField(),
                        c.getWorkflowConditionOperator().name(),
                        c.getExpectedValue()
                )).toList();
        List<WorkflowActionResponseDTO> workflowActions = savedRule.getWorkflowActions()
                .stream()
                .map(a -> new WorkflowActionResponseDTO(
                        a.getActionType().name(),
                        a.getActionConfigJson()
                )).toList();

        return new WorkflowRuleResponseDTO(
                savedRule.getId(),
                savedRule.getName(),
                savedRule.getDescription(),
                savedRule.getWorkflowTriggerType().name(),
                savedRule.isActive(),
                workflowConditions,
                workflowActions,
                savedRule.getCreatedAt()
        );
    }
}
