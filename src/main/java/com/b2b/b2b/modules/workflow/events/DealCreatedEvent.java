package com.b2b.b2b.modules.workflow.events;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;
@Getter
@Setter
public class DealCreatedEvent extends ApplicationEvent
{
    private final Integer dealID;

    public DealCreatedEvent(Object source, Integer dealID) {
        super(source);
        this.dealID = dealID;
    }
}
