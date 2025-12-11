package com.b2b.b2b.modules.workflow.listeners;

import com.b2b.b2b.modules.workflow.events.LeadCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class WorkflowEngineListener {
    Logger logger = LoggerFactory.getLogger(WorkflowEngineListener.class);
    @EventListener
    @Async
    public void handleOnLeadCreatedEvent(LeadCreatedEvent leadCreatedEvent)
    {
        //logger for replaces workflow run service
        logger.info("Run workflow engine for event object{}", leadCreatedEvent.getSource().toString());
        //Before the workflow engine can react to events, create workflow rules first
        //call WorkflowEngineServiceimple.run(leadCreatedEvent.getLead())
    }
}
