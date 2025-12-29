package com.b2b.b2b.modules.workflow.listeners;

import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.crm.deal.entity.Deal;
import com.b2b.b2b.modules.crm.lead.entity.Lead;
import com.b2b.b2b.modules.workflow.entity.WorkflowRule;
import com.b2b.b2b.modules.workflow.enums.WorkflowTriggerType;
import com.b2b.b2b.modules.workflow.events.DealCreatedEvent;
import com.b2b.b2b.modules.workflow.events.DealStatusUpdatedEvent;
import com.b2b.b2b.modules.workflow.events.LeadCreatedEvent;
import com.b2b.b2b.modules.workflow.events.LeadStatusUpdatedEvent;
import com.b2b.b2b.modules.workflow.service.WorkflowEngineService;
import com.b2b.b2b.modules.workflow.service.WorkflowRuleService;
import com.b2b.b2b.modules.workflow.service.WorkflowTarget;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class WorkflowEngineListener {
    private final WorkflowRuleService workflowRuleService;
    private final WorkflowEngineService workflowEngineService;


    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOnLeadCreatedEvent(LeadCreatedEvent event) {
        log.info("Listening on LeadCreatedEvent");
        processWorkflow(event.getLead(), event.getLead().getCompany().getOrganization(), WorkflowTriggerType.LEAD_CREATED);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOnLeadStatusUpdatedEvent(LeadStatusUpdatedEvent event) {
        log.info("Listening on LeadStatusUpdatedEvent");

        if (!event.getOldStatus().equals(event.getNewStatus())) {
            return;
        }
        Lead lead = event.getLead();
        log.info("Workflow Triggered: Lead {} status changed from {} to {}",
                lead.getId(), event.getOldStatus(), event.getNewStatus());

        processWorkflow(lead, lead.getCompany().getOrganization(), WorkflowTriggerType.LEAD_STATUS_UPDATED);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOnDealCreatedEvent(DealCreatedEvent event)
    {   log.info("Listening on DealCreatedEvent");
        processWorkflow(event.getDeal(), event.getDeal().getCompany().getOrganization(), WorkflowTriggerType.DEAL_CREATED);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOnDealStatusUpdatedEvent(DealStatusUpdatedEvent event){
        log.info("Listening on  dealStatusUpdatedEvent");

        if(!event.getOldStatus().equals(event.getNewStatus())) {
            return;
        }
        Deal deal = event.getDeal();

        processWorkflow(deal, deal.getCompany().getOrganization(), WorkflowTriggerType.DEAL_STATUS_UPDATED);
    }

    private void processWorkflow(WorkflowTarget target, Organization org, WorkflowTriggerType type) {

        try {
            List<WorkflowRule> rules = workflowRuleService.getWorkflowRules(org, type, true);
            if (rules.isEmpty()) {
                log.info("No active rules for {} on {}", type, target.getClass().getSimpleName());
            }
            workflowEngineService.run(target, rules);
        } catch (Exception e) {
            log.error("Workflow Engine failed for {} {}: {}",
                    target.getClass().getSimpleName(), type, e.getMessage());
        }
    }
}

