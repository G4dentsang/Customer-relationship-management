package com.b2b.b2b.modules.workflow.events;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class LeadCreatedEvent extends ApplicationEvent {
    private final Integer id;
    public LeadCreatedEvent(Object source, Integer id) {
        super(source);
        this.id = id;
    }


}
