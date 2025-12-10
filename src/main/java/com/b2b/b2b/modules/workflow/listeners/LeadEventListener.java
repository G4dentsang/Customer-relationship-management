package com.b2b.b2b.modules.workflow.listeners;

import com.b2b.b2b.modules.workflow.events.LeadCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class LeadEventListener {
    Logger logger = LoggerFactory.getLogger(LeadEventListener.class);
    @EventListener
    @Async
    public void handleOnLeadCreatedEvent(LeadCreatedEvent leadCreatedEvent)
    {
        logger.info("lead created with id {}", leadCreatedEvent.getId());
        //pipeline starts
        //1 evaluate workflow rule
        //2 trigger pipeline action
        //logging
    }
}
