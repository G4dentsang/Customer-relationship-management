package com.b2b.b2b.modules.workflow.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class LeadCreatedEvent extends ApplicationEvent {
    private final Integer id;
    public LeadCreatedEvent(Object source, Integer id) {
        super(source);
        this.id = id;
    }


}
