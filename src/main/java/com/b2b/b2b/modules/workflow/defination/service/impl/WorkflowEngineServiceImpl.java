package com.b2b.b2b.modules.workflow.defination.service.impl;

import com.b2b.b2b.modules.workflow.exception.WorkflowMaintenanceException;
import com.b2b.b2b.modules.workflow.defination.model.WorkflowAction;
import com.b2b.b2b.modules.workflow.defination.model.WorkflowRule;
import com.b2b.b2b.modules.workflow.defination.service.WorkflowActionService;
import com.b2b.b2b.modules.workflow.defination.service.WorkflowConditionService;
import com.b2b.b2b.modules.workflow.engine.processor.WorkflowEngineService;
import com.b2b.b2b.modules.workflow.defination.service.WorkflowTarget;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorkflowEngineServiceImpl implements WorkflowEngineService {

    private final WorkflowConditionService workflowConditionService;
    private final WorkflowActionService workflowActionService;

    @Override
    @Transactional
    public void run(WorkflowTarget target, List<WorkflowRule> rules) {
        log.info("Starting workflow engine for target: {} with {} rules", target.getClass().getSimpleName(), rules.size());

        for (WorkflowRule rule : rules) {
            if(!rule.isActive()) {
                throw new WorkflowMaintenanceException(String.format("Workflow rule %s is currently in maintenance. You will be notified once it is reactivated.",
                        rule.getName()));
            }
            try {
                boolean allConditionsMet = workflowConditionService.evaluateCondition(rule.getWorkflowConditions(), target);

                if (allConditionsMet) {
                    log.info("Rule '{}' conditions met!. Executing actions.", rule.getName());

                    for (WorkflowAction action : rule.getWorkflowActions()) {
                        workflowActionService.execute(action, target);
                    }
                } else {
                    log.debug("Rule '{}' conditions not met. Skipping.", rule.getName());
                }

            } catch (Exception e) {
                log.error("Error processing Workflow Rule '{}': {}", rule.getName(), e.getMessage());
            }

        }
    }
}
