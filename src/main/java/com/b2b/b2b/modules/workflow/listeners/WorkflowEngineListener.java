package com.b2b.b2b.modules.workflow.listeners;

import com.b2b.b2b.exception.WorkflowMaintenanceException;
import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.crm.deal.entity.Deal;
import com.b2b.b2b.modules.crm.lead.entity.Lead;
import com.b2b.b2b.modules.workflow.entity.WorkflowRule;
import com.b2b.b2b.modules.workflow.enums.WorkflowTriggerType;
import com.b2b.b2b.modules.workflow.events.*;
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
        log.info("Listening on LeadCreatedEvent for Lead ID: {}", event.getLead().getId());
        processWorkflow(event.getLead(), event.getLead().getCompany().getOrganization(), WorkflowTriggerType.LEAD_CREATED);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOnLeadStatusUpdatedEvent(LeadStatusUpdatedEvent event) {
        log.info("Listening on LeadStatusUpdatedEvent for Lead ID: {} ", event.getLead().getId());
        Lead lead = event.getLead();
        processWorkflow(lead, lead.getCompany().getOrganization(), WorkflowTriggerType.LEAD_STATUS_UPDATED);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOnLeadAssignedEvent(LeadAssignedEvent event) {
        log.info("Listening on LeadAssignedEvent for Lead ID: {}", event.getLead().getId());
        Lead lead = event.getLead();
        processWorkflow(lead, lead.getCompany().getOrganization(), WorkflowTriggerType.LEAD_ASSIGNED);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOnLeadDeletedEvent(LeadDeletedEvent event) {
        log.info("Listening on LeadDeletedEvent for Lead ID: {}", event.getLead().getId());
        processWorkflow(event.getLead(), event.getLead().getCompany().getOrganization(), WorkflowTriggerType.LEAD_DELETED);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOnDealCreatedEvent(DealCreatedEvent event) {
        log.info("Listening on DealCreatedEvent for Deal ID: {}", event.getDeal().getId());
        processWorkflow(event.getDeal(), event.getDeal().getCompany().getOrganization(), WorkflowTriggerType.DEAL_CREATED);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOnDealStatusUpdatedEvent(DealStatusUpdatedEvent event){
        log.info("Listening on DealStatusUpdatedEvent  for Deal ID: {}", event.getDeal().getId());
        Deal deal = event.getDeal();
        processWorkflow(deal, deal.getCompany().getOrganization(), WorkflowTriggerType.DEAL_STATUS_UPDATED);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOnDealDeletedEvent(DealDeletedEvent event) {
        log.info("Listening on DealDeletedEvent for Deal ID: {}", event.getDeal().getId());
        processWorkflow(event.getDeal(), event.getDeal().getCompany().getOrganization(), WorkflowTriggerType.DEAL_DELETED);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOnPipelineStageChangeEvent(PipelineStageChangeEvent event) {

        if(event.getEntity() instanceof Lead lead){
            log.info("Lead {} moved from {} to {}",
                    lead.getLeadName(),
                    event.getFromStage().getStageName(),
                    event.getToStage().getStageName());
            processWorkflow(lead, lead.getOrganization(),WorkflowTriggerType.LEAD_STAGE_CHANGED);

        }
        if(event.getEntity() instanceof Deal deal){
            log.info("Deal {} moved from {} to {}",
                    deal.getDealName(),
                    event.getFromStage().getStageName(),
                    event.getToStage().getStageName());
            processWorkflow(deal, deal.getOrganization(),WorkflowTriggerType.DEAL_STAGE_CHANGED);
        }
    }

    private void processWorkflow(WorkflowTarget target, Organization org, WorkflowTriggerType type) {

        try {
            List<WorkflowRule> rules = workflowRuleService.getAllRulesByTriggerType(org, type);
            if (rules.isEmpty()) {
                log.info("No active rules for {} on {}", type, target.getClass().getSimpleName());
            }
            workflowEngineService.run(target, rules);
        } catch (WorkflowMaintenanceException ex) {
            log.warn("Maintenance Alert: {}", ex.getMessage());
        } catch(Exception ex) {
            log.error("Workflow Engine failed: {}", ex.getMessage());
        }
    }
}

