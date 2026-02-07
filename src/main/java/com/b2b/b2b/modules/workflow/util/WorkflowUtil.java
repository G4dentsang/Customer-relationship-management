package com.b2b.b2b.modules.workflow.util;

import com.b2b.b2b.modules.workflow.defination.model.WorkflowAction;
import com.b2b.b2b.modules.workflow.defination.model.WorkflowCondition;
import com.b2b.b2b.modules.workflow.defination.payloads.WorkflowActionResponseDTO;
import com.b2b.b2b.modules.workflow.defination.payloads.WorkflowConditionResponseDTO;
import com.b2b.b2b.modules.workflow.defination.payloads.WorkflowRuleResponseDTO;
import com.b2b.b2b.modules.workflow.defination.model.WorkflowRule;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class WorkflowUtil {

    public WorkflowRuleResponseDTO createWorkflowRuleResponseDTO(WorkflowRule savedRule) {
        List<WorkflowConditionResponseDTO> workflowConditions = savedRule.getWorkflowConditions()
                .stream()
                .map(c -> new WorkflowConditionResponseDTO(
                        c.getId(),
                        c.getField(),
                        c.getWorkflowConditionOperator(),
                        c.getExpectedValue()
                )).toList();
        List<WorkflowActionResponseDTO> workflowActions = savedRule.getWorkflowActions()
                .stream()
                .map(a -> new WorkflowActionResponseDTO(
                        a.getId(),
                        a.getActionType(),
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
    public WorkflowConditionResponseDTO createWorkflowConditionResponseDTO(WorkflowCondition c) {
       return  new WorkflowConditionResponseDTO(
                c.getId(),
                c.getField(),
                c.getWorkflowConditionOperator(),
                c.getExpectedValue());
    }
    public WorkflowActionResponseDTO createWorkflowActionResponseDTO(WorkflowAction a) {
        return new WorkflowActionResponseDTO(
                a.getId(),
                a.getActionType(),
                a.getActionConfigJson()
        );
    }

}
