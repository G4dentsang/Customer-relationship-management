package com.b2b.b2b.modules.workflow.util;

import com.b2b.b2b.modules.workflow.dto.WorkflowActionResponseDTO;
import com.b2b.b2b.modules.workflow.dto.WorkflowConditionResponseDTO;
import com.b2b.b2b.modules.workflow.dto.WorkflowRuleResponseDTO;
import com.b2b.b2b.modules.workflow.entity.WorkflowRule;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class WorkflowUtil {

    public WorkflowRuleResponseDTO createWorkflowRuleResponseDTO(WorkflowRule savedRule) {
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
