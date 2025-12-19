package com.b2b.b2b.modules.workflow.listeners;

import com.b2b.b2b.modules.workflow.entity.WorkflowRule;
import com.b2b.b2b.modules.workflow.enums.WorkflowTriggerType;
import com.b2b.b2b.modules.workflow.events.LeadCreatedEvent;
import com.b2b.b2b.modules.workflow.service.WorkflowEngineService;
import com.b2b.b2b.modules.workflow.service.WorkflowRuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WorkflowEngineListener {
    Logger logger = LoggerFactory.getLogger(WorkflowEngineListener.class);
    private final WorkflowRuleService workflowRuleService;

    private final WorkflowEngineService workflowEngineService;
    WorkflowEngineListener(WorkflowEngineService workflowEngineService, WorkflowRuleService workflowRuleService) {
        this.workflowEngineService = workflowEngineService;
        this.workflowRuleService = workflowRuleService;
    }


    @EventListener
    public void handleOnLeadCreatedEvent(LeadCreatedEvent leadCreatedEvent)
    {
        logger.info("Run workflow engine for event object{}", leadCreatedEvent.getLead().toString());
        Integer orgId = leadCreatedEvent.getLead().getCompany().getOrganization().getOrganizationId();
        List<WorkflowRule> rules = workflowRuleService.getWorkflowRules(orgId, WorkflowTriggerType.LEAD_CREATED, true);
        if(rules.isEmpty()){
            logger.info("No rules found for lead id {}", orgId);}
        else{workflowEngineService.run(leadCreatedEvent.getLead(), rules);}


    }
}
