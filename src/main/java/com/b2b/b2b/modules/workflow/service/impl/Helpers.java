package com.b2b.b2b.modules.workflow.service.impl;

import com.b2b.b2b.modules.workflow.entity.WorkflowAction;
import com.b2b.b2b.modules.workflow.entity.WorkflowCondition;
import com.b2b.b2b.modules.workflow.entity.WorkflowRule;
import com.b2b.b2b.modules.workflow.payloads.*;
import com.b2b.b2b.modules.workflow.service.WorkflowTarget;
import com.b2b.b2b.modules.workflow.util.WorkflowUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
class Helpers {

    private final WorkflowUtil workflowUtil;
    private final ModelMapper modelMapper;

    void syncConditions(WorkflowRule rule, List<WorkflowConditionDTO> request) {
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

    void syncActions(WorkflowRule rule, List<WorkflowActionDTO> request) {
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

    WorkflowRule convertToEntity(WorkflowRuleCreateDTO request) {
        WorkflowRule workflowRule = new WorkflowRule();
        workflowRule.setName(request.getName());
        workflowRule.setDescription(request.getDescription());
        workflowRule.setWorkflowTriggerType(request.getWorkflowTriggerType());
        workflowRule.setActive(request.isActive());
        return workflowRule;
    }

    void bindConditions(WorkflowRule rule, WorkflowRuleCreateDTO request) {
        List<WorkflowCondition> conditions = request.getWorkflowConditions()
                .stream()
                .map(c -> {
                    WorkflowCondition condition = modelMapper.map(c, WorkflowCondition.class);
                    condition.setWorkflowRule(rule);
                    return condition;
                }).toList();
        rule.setWorkflowConditions(conditions);
    }

    void bindActions(WorkflowRule rule, WorkflowRuleCreateDTO request) {
        List<WorkflowAction> actions = request.getWorkflowActions()
                .stream()
                .map(a -> {
                    WorkflowAction workflowAction = modelMapper.map(a, WorkflowAction.class);
                    workflowAction.setWorkflowRule(rule);
                    return workflowAction;
                }).toList();
        rule.setWorkflowActions(actions);
    }

    String getDBFieldValue(WorkflowTarget target, String fieldName) {

        try {
            BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(target);
            Object value = beanWrapper.getPropertyValue(fieldName);

            return value != null ? String.valueOf(value) : null;

        } catch (Exception e) {
            log.error("Could not find field {} on class {}", fieldName, target.getClass().getSimpleName());
            return null;
        }
    }

    Page<WorkflowRuleResponseDTO> toDTORuleList(Page<WorkflowRule> rules) {
        return rules.map(workflowUtil::createWorkflowRuleResponseDTO);
    }

    List<WorkflowConditionResponseDTO> toDTOConditionList(List<WorkflowCondition> conditions) {
        return conditions.stream().map(workflowUtil::createWorkflowConditionResponseDTO).toList();
    }

    List<WorkflowActionResponseDTO> toDTOActionList(List<WorkflowAction> actions) {
        return actions.stream()
                .map(workflowUtil::createWorkflowActionResponseDTO)
                .toList();

    }
}
