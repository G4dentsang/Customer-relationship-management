package com.b2b.b2b.modules.workflow.engine.listener;

import com.b2b.b2b.modules.crm.deal.api.event.DealCreatedEvent;
import com.b2b.b2b.modules.crm.deal.api.event.DealDeletedEvent;
import com.b2b.b2b.modules.crm.deal.api.event.DealPipelineStageChangeEvent;
import com.b2b.b2b.modules.crm.deal.api.event.DealStatusUpdatedEvent;
import com.b2b.b2b.modules.crm.lead.api.event.*;
import com.b2b.b2b.modules.workflow.exception.WorkflowMaintenanceException;
import com.b2b.b2b.modules.organization.model.Organization;
import com.b2b.b2b.modules.crm.deal.internal.infrastructure.persistence.Deal;
import com.b2b.b2b.modules.crm.lead.internal.infrastructure.persistence.Lead;
import com.b2b.b2b.modules.workflow.defination.model.WorkflowRule;
import com.b2b.b2b.modules.workflow.enums.WorkflowTriggerType;
import com.b2b.b2b.modules.workflow.engine.processor.WorkflowEngineService;
import com.b2b.b2b.modules.workflow.defination.service.WorkflowRuleService;
import com.b2b.b2b.modules.workflow.defination.service.WorkflowTarget;
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
        log.info("Listening on LeadCreatedEvent for Lead ID: {}", event.lead().getId());
        processWorkflow(event.lead(), event.lead().getOrganization(), WorkflowTriggerType.LEAD_CREATED);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOnLeadStatusUpdatedEvent(LeadStatusUpdatedEvent event) {
        log.info("Listening on LeadStatusUpdatedEvent for Lead ID: {} ", event.lead().getId());
        Lead lead = event.lead();
        processWorkflow(lead, lead.getOrganization(), WorkflowTriggerType.LEAD_STATUS_UPDATED);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOnLeadAssignedEvent(LeadAssignedEvent event) {
        log.info("Listening on LeadAssignedEvent for Lead ID: {}", event.lead().getId());
        Lead lead = event.lead();
        processWorkflow(lead, lead.getOrganization(), WorkflowTriggerType.LEAD_ASSIGNED);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOnLeadStatusChangedEvent(LeadAssignedEvent event) {
        log.info("Listening on LeadStatusChangedEvent for Lead ID: {}", event.lead().getId());
        Lead lead = event.lead();
        processWorkflow(lead, lead.getOrganization(), WorkflowTriggerType.LEAD_ASSIGNED);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOnLeadDeletedEvent(LeadDeletedEvent event) {
        log.info("Listening on LeadDeletedEvent for Lead ID: {}", event.lead().getId());
        processWorkflow(event.lead(), event.lead().getOrganization(), WorkflowTriggerType.LEAD_DELETED);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOnDealCreatedEvent(DealCreatedEvent event) {
        log.info("Listening on DealCreatedEvent for Deal ID: {}", event.deal().getId());
        processWorkflow(event.deal(), event.deal().getOrganization(), WorkflowTriggerType.DEAL_CREATED);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOnDealStatusUpdatedEvent(DealStatusUpdatedEvent event){
        log.info("Listening on DealStatusUpdatedEvent  for Deal ID: {}", event.deal().getId());
        Deal deal = event.deal();
        processWorkflow(deal, deal.getOrganization(), WorkflowTriggerType.DEAL_STATUS_UPDATED);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOnDealDeletedEvent(DealDeletedEvent event) {
        log.info("Listening on DealDeletedEvent for Deal ID: {}", event.deal().getId());
        processWorkflow(event.deal(), event.deal().getOrganization(), WorkflowTriggerType.DEAL_DELETED);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOnLeadStageChangeEvent(LeadPipelineStageChangeEvent event) {
        log.info("Lead {} moved from {} to {}",
                event.lead().getId(),
                event.fromStage().getStageName(),
                event.toStage().getStageName());
        processWorkflow(event.lead(), event.lead().getOrganization(), WorkflowTriggerType.LEAD_STAGE_CHANGED);

    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOnDealStageChangeEvent(DealPipelineStageChangeEvent event) {
        log.info("Deal {} moved from {} to {}",
                event.deal().getId(),
                event.fromStage().getStageName(),
                event.toStage().getStageName());
        processWorkflow(event.deal(), event.deal().getOrganization(), WorkflowTriggerType.LEAD_STAGE_CHANGED);

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
        } catch (Exception ex) {
            log.error("Workflow Engine failed: {}", ex.getMessage());
        }
    }
}

