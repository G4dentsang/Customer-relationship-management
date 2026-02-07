package com.b2b.b2b.modules.workflow.engine.action;

import com.b2b.b2b.modules.notification.service.EmailService;
import com.b2b.b2b.modules.workflow.defination.model.WorkflowAction;
import com.b2b.b2b.modules.workflow.enums.WorkflowActionType;
import com.b2b.b2b.modules.workflow.defination.service.WorkflowTarget;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class EmailAction implements WorkflowActionHandler
{
    private final EmailService emailService;//should change name to email service more general use cases
    @Override
    public WorkflowActionType getType() {
        return WorkflowActionType.SEND_EMAIL;
    }

    @Override
    public void handle(WorkflowAction action, WorkflowTarget target) {
     log.info("Sending email via workflow ....");
    }
}
