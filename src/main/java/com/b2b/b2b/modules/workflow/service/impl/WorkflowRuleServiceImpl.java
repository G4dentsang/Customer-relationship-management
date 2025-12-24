package com.b2b.b2b.modules.workflow.service.impl;

import com.b2b.b2b.exception.APIException;
import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.workflow.dto.WorkflowRuleCreateDTO;
import com.b2b.b2b.modules.workflow.dto.WorkflowRuleResponseDTO;
import com.b2b.b2b.modules.workflow.entity.WorkflowAction;
import com.b2b.b2b.modules.workflow.entity.WorkflowCondition;
import com.b2b.b2b.modules.workflow.entity.WorkflowRule;
import com.b2b.b2b.modules.workflow.enums.WorkflowTriggerType;
import com.b2b.b2b.modules.workflow.repository.WorkflowRuleRepository;
import com.b2b.b2b.modules.workflow.service.WorkflowRuleService;
import com.b2b.b2b.modules.workflow.util.WorkflowUtil;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class WorkflowRuleServiceImpl implements WorkflowRuleService {
    private final WorkflowRuleRepository workflowRuleRepository;
    private final WorkflowUtil workflowUtil;
    public WorkflowRuleServiceImpl(WorkflowRuleRepository workflowRuleRepository, WorkflowUtil workflowUtil) {
        this.workflowRuleRepository = workflowRuleRepository;
        this.workflowUtil = workflowUtil;
    }
    @Override
    public List<WorkflowRule> getWorkflowRules(Integer orgId, WorkflowTriggerType triggerType, Boolean isActive) {
        return workflowRuleRepository.findByOrganization_organizationIdAndWorkflowTriggerTypeAndIsActive(orgId, triggerType, isActive);
    }

    @Override
    public List<WorkflowRuleResponseDTO> getAllWorkflowRules(User user) {
        Organization organization = user.getUserOrganizations()
                .stream()
                .filter((userOrganization -> userOrganization.isPrimary()))
                .findFirst()
                .orElseThrow(()-> new APIException("User has no primary organization"))
                .getOrganization();
        List<WorkflowRule> workflowRuleDB = workflowRuleRepository.findAllByOrganization(organization);
        return  workflowRuleDB.stream().map(rule ->  workflowUtil.createWorkflowRuleResponseDTO(rule)).toList();
    }

    @Override
    public WorkflowRuleResponseDTO getWorkflowRule(Integer workflowId, User user) {
        Organization organization = user.getUserOrganizations()
                .stream()
                .filter((userOrganization -> userOrganization.isPrimary()))
                .findFirst()
                .orElseThrow(()-> new APIException("User has no primary organization"))
                .getOrganization();
        WorkflowRule workflowRule = workflowRuleRepository.findByIdAndOrganization(workflowId,organization);
        return workflowUtil.createWorkflowRuleResponseDTO(workflowRule);
    }

    @Override
    public WorkflowRuleResponseDTO saveWorkflowRule(WorkflowRuleCreateDTO workflowRuleCreateDTO, User user) {
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
        return workflowUtil.createWorkflowRuleResponseDTO(savedRule);
    }

    @Override
    public WorkflowRuleResponseDTO activateWorkflowRule(Integer ruleId, User user) {
        Organization organization = user.getUserOrganizations()
                .stream()
                .filter((userOrganization -> userOrganization.isPrimary()))
                .findFirst()
                .orElseThrow(()-> new APIException("User has no primary organization"))
                .getOrganization();
        WorkflowRule workflowRule = workflowRuleRepository.findByIdAndOrganization(ruleId,organization);
        if(!workflowRule.isActive()){
            workflowRule.setActive(true);
            workflowRuleRepository.save(workflowRule);
        }else{
            throw new APIException("Workflow Rule has already been activated");
        }
        return workflowUtil.createWorkflowRuleResponseDTO(workflowRule);
    }

    @Override
    public WorkflowRuleResponseDTO deactivateWorkflowRule(Integer ruleId, User user) {
        Organization organization = user.getUserOrganizations()
                .stream()
                .filter((userOrganization -> userOrganization.isPrimary()))
                .findFirst()
                .orElseThrow(()-> new APIException("User has no primary organization"))
                .getOrganization();
        WorkflowRule workflowRule = workflowRuleRepository.findByIdAndOrganization(ruleId,organization);
        if(workflowRule.isActive()){
            workflowRule.setActive(false);
            workflowRuleRepository.save(workflowRule);
        }else{
            throw new APIException("Workflow Rule has already been deactivated");
        }
        return workflowUtil.createWorkflowRuleResponseDTO(workflowRule);
    }
}
