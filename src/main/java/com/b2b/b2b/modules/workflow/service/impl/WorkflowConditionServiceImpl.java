package com.b2b.b2b.modules.workflow.service.impl;

import com.b2b.b2b.exception.ResourceNotFoundException;
import com.b2b.b2b.exception.WorkflowMaintenanceException;
import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.workflow.entity.WorkflowCondition;
import com.b2b.b2b.modules.workflow.entity.WorkflowRule;
import com.b2b.b2b.modules.workflow.payloads.WorkflowConditionDTO;
import com.b2b.b2b.modules.workflow.payloads.WorkflowConditionResponseDTO;
import com.b2b.b2b.modules.workflow.repository.WorkflowConditionRepository;
import com.b2b.b2b.modules.workflow.repository.WorkflowRuleRepository;
import com.b2b.b2b.modules.workflow.service.WorkflowConditionService;
import com.b2b.b2b.modules.workflow.service.WorkflowTarget;
import com.b2b.b2b.modules.workflow.util.WorkflowUtil;
import com.b2b.b2b.shared.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorkflowConditionServiceImpl implements WorkflowConditionService
{
    private final WorkflowRuleRepository workflowRuleRepository;
    private final WorkflowConditionRepository workflowConditionRepository;
    private final AuthUtil authUtil;
    private final ModelMapper modelMapper;
    private final WorkflowUtil workflowUtil;

    @Override
    public boolean evaluateCondition(List<WorkflowCondition> conditions, WorkflowTarget target) {
        return conditions.stream().allMatch(c -> {
            String actual = getDBFieldValue(target, c.getField());
            return c.getWorkflowConditionOperator().apply(actual, c.getExpectedValue());
        });
    }

    @Override
    @Transactional
    public List<WorkflowConditionResponseDTO> addConditions(Integer id, List<WorkflowConditionDTO> request, User user) {

        WorkflowRule rule = workflowRuleRepository.findByIdAndOrganization(id, getOrg(user))
                .orElseThrow(() -> new ResourceNotFoundException("Workflow", "id", id));

        List<WorkflowCondition> newConditions = request.stream()
                        .map(dto -> {
                            WorkflowCondition condition = modelMapper.map(dto, WorkflowCondition.class);
                            condition.setWorkflowRule(rule);
                            return  condition;
                        }).toList();

        List<WorkflowCondition> savedConditions =   workflowConditionRepository.saveAll(newConditions);
        return toDTOList(savedConditions);
    }

    @Override
    @Transactional
    public void deleteCondition(Integer ruleId, Long conditionId, User user) {
        Organization org = getOrg(user);
        WorkflowRule rule = workflowRuleRepository.findByIdAndOrganization(ruleId, org)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow", "id", ruleId));

        if(rule.isActive()) throw new WorkflowMaintenanceException("Cannot remove logic from an ACTIVE rule. Please deactivate first.");

        WorkflowCondition condition = workflowConditionRepository.findByIdAndWorkflowRule(conditionId, rule)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow", "id", conditionId));

        rule.getWorkflowConditions().remove(condition);
        workflowConditionRepository.delete(condition);
        workflowRuleRepository.save(rule);

        log.info("Condition {} removed from Rule {} by {}", conditionId, ruleId, user.getEmail());
    }

    /******************Helper methods************************/

    private Organization getOrg(User user) {
        return authUtil.getPrimaryOrganization(user);
    }

    private List<WorkflowConditionResponseDTO> toDTOList(List<WorkflowCondition> conditions) {
        return conditions.stream().map(workflowUtil::createWorkflowConditionResponseDTO).toList();
    }

    private String getDBFieldValue(WorkflowTarget target, String fieldName) {

        try {
            BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(target);
            Object value = beanWrapper.getPropertyValue(fieldName);

            return value != null ? String.valueOf(value) : null;

        } catch (Exception e) {
            log.error("Could not find field {} on class {}", fieldName, target.getClass().getSimpleName());
            return null;
        }
    }


}
