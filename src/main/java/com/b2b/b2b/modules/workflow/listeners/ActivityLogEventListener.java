package com.b2b.b2b.modules.workflow.listeners;

import com.b2b.b2b.modules.workflow.events.LeadCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ActivityLogEventListener {
    Logger logger = LoggerFactory.getLogger(ActivityLogEventListener.class);
    @EventListener
    @Async
    public void handleOnLeadCreatedEvent(LeadCreatedEvent leadCreatedEvent)
    {
        //logger for replaces ActivityLog service
        logger.info("Activity logging  with id {}", leadCreatedEvent.lead().getId());
        //other domain related task
    }
}
