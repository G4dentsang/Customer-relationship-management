package com.b2b.b2b.modules.workflow.service;

import com.b2b.b2b.modules.crm.lead.entity.Lead;
import com.b2b.b2b.modules.workflow.entity.WorkflowCondition;

import java.util.List;

public interface WorkflowConditionEvaluatorService {
    boolean evaluateCondition(List<WorkflowCondition> conditions, Lead lead);
}
