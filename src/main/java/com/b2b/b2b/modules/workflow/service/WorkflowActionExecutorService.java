package com.b2b.b2b.modules.workflow.service;

import com.b2b.b2b.modules.crm.lead.entity.Lead;
import com.b2b.b2b.modules.workflow.entity.WorkflowAction;

public interface WorkflowActionExecutorService {
    void execute(WorkflowAction action, Lead lead);
    void executeEmailAction(WorkflowAction action, Lead lead);
    void executeAssignmentAction(WorkflowAction action, Lead lead);
}
