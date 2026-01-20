package com.b2b.b2b.modules.workflow.service.impl;

import com.b2b.b2b.exception.ResourceNotFoundException;
import com.b2b.b2b.exception.WorkflowMaintenanceException;
import com.b2b.b2b.modules.workflow.entity.WorkflowCondition;
import com.b2b.b2b.modules.workflow.entity.WorkflowRule;
import com.b2b.b2b.modules.workflow.payloads.WorkflowConditionDTO;
import com.b2b.b2b.modules.workflow.payloads.WorkflowConditionResponseDTO;
import com.b2b.b2b.modules.workflow.repository.WorkflowConditionRepository;
import com.b2b.b2b.modules.workflow.repository.WorkflowRuleRepository;
import com.b2b.b2b.modules.workflow.service.WorkflowConditionService;
import com.b2b.b2b.modules.workflow.service.WorkflowTarget;
import com.b2b.b2b.shared.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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
    private final Helpers helpers;

    @Override
    public boolean evaluateCondition(List<WorkflowCondition> conditions, WorkflowTarget target) {
        return conditions.stream().allMatch(c -> {
            String actual = helpers.getDBFieldValue(target, c.getField());
            return c.getWorkflowConditionOperator().apply(actual, c.getExpectedValue());
        });
    }

    @Override
    @Transactional
    public List<WorkflowConditionResponseDTO> addConditions(Integer id, List<WorkflowConditionDTO> request) {
        WorkflowRule rule = workflowRuleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow", "id", id));

        List<WorkflowCondition> newConditions = request.stream()
                        .map(dto -> {
                            WorkflowCondition condition = modelMapper.map(dto, WorkflowCondition.class);
                            condition.setWorkflowRule(rule);
                            return  condition;
                        }).toList();

        List<WorkflowCondition> savedConditions =   workflowConditionRepository.saveAll(newConditions);
        return helpers.toDTOConditionList(savedConditions);
    }

    @Override
    @Transactional
    public void deleteCondition(Integer ruleId, Long conditionId) {
        WorkflowRule rule = workflowRuleRepository.findById(ruleId)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow", "id", ruleId));

        if(rule.isActive()) throw new WorkflowMaintenanceException("Cannot remove logic from an ACTIVE rule. Please deactivate first.");

        WorkflowCondition condition = workflowConditionRepository.findByIdAndWorkflowRule(conditionId, rule)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow", "id", conditionId));

        rule.getWorkflowConditions().remove(condition);
        workflowConditionRepository.delete(condition);
        workflowRuleRepository.save(rule);

        log.info("Condition {} removed from Rule {} by {}", conditionId, ruleId, authUtil.loggedInUser().getEmail());
    }
}
