package com.b2b.b2b.shared;


import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class DomainEventPublisher
{
    private final ApplicationEventPublisher applicationEventPublisher;
    public  DomainEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void publishEvent(ApplicationEvent event){
        applicationEventPublisher.publishEvent(event);
    }
    public void publishEvent(Object event){
        applicationEventPublisher.publishEvent(event);
    }
}
