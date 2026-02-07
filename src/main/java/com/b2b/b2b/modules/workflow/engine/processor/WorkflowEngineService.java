package com.b2b.b2b.modules.workflow.engine.processor;

import com.b2b.b2b.modules.workflow.defination.model.WorkflowRule;
import com.b2b.b2b.modules.workflow.defination.service.WorkflowTarget;

import java.util.List;

public interface WorkflowEngineService {
    void run(WorkflowTarget target, List<WorkflowRule> rules);
}
