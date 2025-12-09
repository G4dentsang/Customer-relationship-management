package com.b2b.b2b.modules.workflow.events;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;

public class DomainEventPublisher implements ApplicationEventPublisher
{
    @Override
    public void publishEvent(ApplicationEvent event) {
        ApplicationEventPublisher.super.publishEvent(event);
    }

    @Override
    public void publishEvent(Object event) {

    }
}
