package com.b2b.b2b.modules.workflow.events;

import org.springframework.context.ApplicationEvent;

public class LeadStageUpdatedEvent extends ApplicationEvent
{

    public LeadStageUpdatedEvent(Object source) {
        super(source);
    }
}

