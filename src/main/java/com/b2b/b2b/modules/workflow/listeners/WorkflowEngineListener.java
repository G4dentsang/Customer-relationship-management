package com.b2b.b2b.modules.workflow.listeners;

import com.b2b.b2b.modules.workflow.events.LeadCreatedEvent;
import com.b2b.b2b.modules.workflow.service.WorkflowEngineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WorkflowEngineListener {
    Logger logger = LoggerFactory.getLogger(WorkflowEngineListener.class);

    private final WorkflowEngineService workflowEngineService;
    WorkflowEngineListener( WorkflowEngineService workflowEngineService) {
        this.workflowEngineService = workflowEngineService;
    }


    @EventListener
    @Async
    public void handleOnLeadCreatedEvent(LeadCreatedEvent leadCreatedEvent)
    {
        logger.info("Run workflow engine for event object{}", leadCreatedEvent.getLead().toString());
        workflowEngineService.run(leadCreatedEvent.getLead());


    }
}
