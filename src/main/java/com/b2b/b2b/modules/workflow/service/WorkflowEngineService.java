package com.b2b.b2b.modules.workflow.service;

import com.b2b.b2b.modules.crm.lead.entity.Lead;
import com.b2b.b2b.modules.workflow.entity.WorkflowRule;

import java.util.List;

public interface WorkflowEngineService {
    void run(Lead lead, List<WorkflowRule> rules);
}
