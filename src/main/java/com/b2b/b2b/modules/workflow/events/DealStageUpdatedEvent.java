package com.b2b.b2b.modules.workflow.events;

import org.springframework.context.ApplicationEvent;

public class DealStageUpdatedEvent extends ApplicationEvent
{
    public DealStageUpdatedEvent(Object source) {
        super(source);
    }
}
