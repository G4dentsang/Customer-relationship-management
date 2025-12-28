package com.b2b.b2b.modules.workflow.service;


import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.workflow.entity.WorkflowCondition;
import com.b2b.b2b.modules.workflow.payloads.WorkflowConditionDTO;
import com.b2b.b2b.modules.workflow.payloads.WorkflowConditionResponseDTO;

import java.util.List;

public interface WorkflowConditionService {
    boolean evaluateCondition(List<WorkflowCondition> conditions, WorkflowTarget target);
    List<WorkflowConditionResponseDTO> addConditions(Integer ruleId, List<WorkflowConditionDTO> conditions, User user);
}
