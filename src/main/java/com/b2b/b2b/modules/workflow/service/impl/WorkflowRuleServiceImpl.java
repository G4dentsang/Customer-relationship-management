package com.b2b.b2b.modules.workflow.service.impl;

import com.b2b.b2b.exception.ResourceNotFoundException;
import com.b2b.b2b.exception.WorkflowMaintenanceException;
import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.workflow.payloads.*;
import com.b2b.b2b.modules.workflow.entity.WorkflowAction;
import com.b2b.b2b.modules.workflow.entity.WorkflowCondition;
import com.b2b.b2b.modules.workflow.entity.WorkflowRule;
import com.b2b.b2b.modules.workflow.enums.WorkflowTriggerType;
import com.b2b.b2b.modules.workflow.repository.WorkflowRuleRepository;
import com.b2b.b2b.modules.workflow.service.WorkflowRuleService;
import com.b2b.b2b.modules.workflow.util.WorkflowUtil;
import com.b2b.b2b.shared.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowRuleServiceImpl implements WorkflowRuleService {

    private final WorkflowRuleRepository workflowRuleRepository;
    private final WorkflowUtil workflowUtil;
    private final AuthUtil authUtil;
    private final ModelMapper modelMapper;

    @Override
    public List<WorkflowRule> getActiveWorkflowRules(Organization org, WorkflowTriggerType type, Boolean isActive) {
        return workflowRuleRepository.findByOrganizationAndWorkflowTriggerTypeAndIsActive(org, type, isActive);
    }

    @Override
    public List<WorkflowRule> getAllRulesByTriggerType(Organization org, WorkflowTriggerType type) {
        return workflowRuleRepository.findByOrganizationAndWorkflowTriggerType(org, type);
    }

    @Override
    public List<WorkflowRuleResponseDTO> getAllWorkflowRules(User user) {
        return toDTOList(workflowRuleRepository.findAllByOrganization(getOrg(user)));
    }

    @Override
    public WorkflowRuleResponseDTO getWorkflowRule(Integer id, User user) {
        WorkflowRule response = workflowRuleRepository.findByIdAndOrganization(id, getOrg(user))
                .orElseThrow(() -> new ResourceNotFoundException("Workflow", "id", id));
        return workflowUtil.createWorkflowRuleResponseDTO(response);
    }

    @Override
    @Transactional
    public WorkflowRuleResponseDTO create(WorkflowRuleCreateDTO request, User user) {
        WorkflowRule rule = convertToEntity(request);

        rule.setOrganization(getOrg(user));
        bindConditions(rule, request);
        bindActions(rule, request);

        return workflowUtil.createWorkflowRuleResponseDTO(workflowRuleRepository.save(rule));
    }

    @Override
    @Transactional
    public WorkflowRuleResponseDTO toggleStatus(Integer id, User user, Boolean status) {
        WorkflowRule rule = workflowRuleRepository.findByIdAndOrganization(id, getOrg(user))
                .orElseThrow(() -> new ResourceNotFoundException("Workflow", "id", id));

        if (rule.isActive() == (status)) {
            return workflowUtil.createWorkflowRuleResponseDTO(rule);
        }
        rule.setActive(status);
        WorkflowRule updatedRule = workflowRuleRepository.save(rule);

        if (status) {
            //notificationService.broadcastToTeam(("Workflow " + rule.getName() + "' is now LIVE.");
            log.info("Workflow {} activated by {}", id, user.getEmail());
            log.info("notification is send");
        } else {
            //notificationService.broadcastToTeam(("Workflow " + rule.getName() + "' is currently DEACTIVATED.");
            log.info("Workflow {} placed in Maintenance Mode(DEACTIVATED) by {}", id, user.getEmail());
            log.info("notification is send");
        }

        return workflowUtil.createWorkflowRuleResponseDTO(updatedRule);
    }

    @Override
    public WorkflowRuleResponseDTO updateMetaData(Integer id, WorkflowRuleUpdateMetaDataDTO request, User user) {
        WorkflowRule rule = workflowRuleRepository.findByIdAndOrganization(id, getOrg(user))
                .orElseThrow(() -> new ResourceNotFoundException("Workflow", "id", id));
        rule.setName(request.getName());
        rule.setDescription(request.getDescription());
        return workflowUtil.createWorkflowRuleResponseDTO(workflowRuleRepository.save(rule));
    }

    @Override
    public WorkflowRuleResponseDTO updateLogic(Integer id, WorkflowRuleUpdateDTO request, User user) {
        WorkflowRule rule = workflowRuleRepository.findByIdAndOrganization(id, getOrg(user))
                .orElseThrow(() -> new ResourceNotFoundException("Workflow", "id", id));
        if (rule.isActive())
            throw new WorkflowMaintenanceException("Workflow is currently LIVE. You must deactivate it before saving logic changes.");

        rule.setWorkflowTriggerType(request.getWorkflowTriggerType());
        syncConditions(rule, request.getWorkflowConditions());
        syncActions(rule, request.getWorkflowActions());

        return workflowUtil.createWorkflowRuleResponseDTO(workflowRuleRepository.save(rule));
    }

    @Override
    @Transactional
    public void delete(Integer id, User user) {
        WorkflowRule rule = workflowRuleRepository.findByIdAndOrganization(id, getOrg(user))
                .orElseThrow(() -> new ResourceNotFoundException("Workflow", "id", id));
        if (rule.isActive())
            throw new WorkflowMaintenanceException("Cannot delete an ACTIVE rule. Please deactivate it first.");
        workflowRuleRepository.delete(rule);
        //notificationService.broadcastToTeam(("Workflow " + rule.getName() + " is deleted by " + user.getEmail);
        log.info("Workflow Rule '{}' and all associated logic deleted by {}", rule.getName(), user.getEmail());
        log.info("notification is send"); //to remove later after postman

    }

    private void syncConditions(WorkflowRule rule, List<WorkflowConditionDTO> request) {
        List<WorkflowCondition> existing = rule.getWorkflowConditions();
        for (WorkflowConditionDTO condition : request) {
            if (condition.getId() != null) {
                existing.stream()
                        .filter(c -> c.getId().equals(condition.getId()))
                        .findFirst()
                        .ifPresent(entity -> {
                            entity.setField(condition.getField());
                            entity.setWorkflowConditionOperator(condition.getWorkflowConditionOperator());
                            entity.setExpectedValue(condition.getExpectedValue());
                        });
            }
        }
    }

    private void syncActions(WorkflowRule rule, List<WorkflowActionDTO> request) {
        List<WorkflowAction> existing = rule.getWorkflowActions();
        for (WorkflowActionDTO action : request) {
            if (action.getId() != null) {
                existing.stream()
                        .filter(c -> c.getId().equals(action.getId()))
                        .findFirst()
                        .ifPresent(entity -> {
                            entity.setActionType(action.getWorkflowActionType());
                            entity.setActionConfigJson(action.getActionConfigJson());
                        });
            }
        }
    }

    /******************Helper methods************************/

    private Organization getOrg(User user) {
        return authUtil.getPrimaryOrganization(user);
    }

    private List<WorkflowRuleResponseDTO> toDTOList(List<WorkflowRule> rules) {
        return rules.stream().map(workflowUtil::createWorkflowRuleResponseDTO).toList();
    }

    private WorkflowRule convertToEntity(WorkflowRuleCreateDTO request) {
        WorkflowRule workflowRule = new WorkflowRule();
        workflowRule.setName(request.getName());
        workflowRule.setDescription(request.getDescription());
        workflowRule.setWorkflowTriggerType(request.getWorkflowTriggerType());
        workflowRule.setActive(request.isActive());
        return workflowRule;
    }

    private void bindConditions(WorkflowRule rule, WorkflowRuleCreateDTO request) {
        List<WorkflowCondition> conditions = request.getWorkflowConditions()
                .stream()
                .map(c -> {
                    WorkflowCondition condition = modelMapper.map(c, WorkflowCondition.class);
                    condition.setWorkflowRule(rule);
                    return condition;
                }).toList();
        rule.setWorkflowConditions(conditions);
    }

    private void bindActions(WorkflowRule rule, WorkflowRuleCreateDTO request) {
        List<WorkflowAction> actions = request.getWorkflowActions()
                .stream()
                .map(a -> {
                    WorkflowAction workflowAction = modelMapper.map(a, WorkflowAction.class);
                    workflowAction.setWorkflowRule(rule);
                    return workflowAction;
                }).toList();
        rule.setWorkflowActions(actions);
    }

}