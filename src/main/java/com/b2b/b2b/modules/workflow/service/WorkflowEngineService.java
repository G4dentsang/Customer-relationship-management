package com.b2b.b2b.modules.workflow.service;

import com.b2b.b2b.modules.workflow.entity.WorkflowRule;

import java.util.List;

public interface WorkflowEngineService {
    void run(WorkflowTarget target, List<WorkflowRule> rules);
}
