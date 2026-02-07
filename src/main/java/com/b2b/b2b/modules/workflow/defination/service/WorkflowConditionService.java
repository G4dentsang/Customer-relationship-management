package com.b2b.b2b.modules.workflow.defination.service;


import com.b2b.b2b.modules.workflow.defination.model.WorkflowCondition;
import com.b2b.b2b.modules.workflow.defination.payloads.WorkflowConditionDTO;
import com.b2b.b2b.modules.workflow.defination.payloads.WorkflowConditionResponseDTO;

import java.util.List;

public interface WorkflowConditionService {
    boolean evaluateCondition(List<WorkflowCondition> conditions, WorkflowTarget target);
    List<WorkflowConditionResponseDTO> addConditions(Integer ruleId, List<WorkflowConditionDTO> conditions);
    void deleteCondition(Integer ruleId, Long conditionId);
}
