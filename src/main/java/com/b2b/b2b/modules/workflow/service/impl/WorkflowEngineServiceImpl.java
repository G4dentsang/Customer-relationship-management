package com.b2b.b2b.modules.workflow.service.impl;

import com.b2b.b2b.modules.workflow.entity.WorkflowAction;
import com.b2b.b2b.modules.workflow.entity.WorkflowCondition;
import com.b2b.b2b.modules.workflow.entity.WorkflowRule;
import com.b2b.b2b.modules.workflow.service.WorkflowActionService;
import com.b2b.b2b.modules.workflow.service.WorkflowConditionService;
import com.b2b.b2b.modules.workflow.service.WorkflowEngineService;
import com.b2b.b2b.modules.workflow.service.WorkflowTarget;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorkflowEngineServiceImpl implements WorkflowEngineService {

    private final WorkflowConditionService workflowConditionService;
    private final WorkflowActionService workflowActionService;

    @Override
    public void run(WorkflowTarget target, List<WorkflowRule> rules) {
        log.info("Starting workflow engine for target: {} with {} rules", target.getClass().getSimpleName(), rules.size());

        for (WorkflowRule rule : rules) {

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
