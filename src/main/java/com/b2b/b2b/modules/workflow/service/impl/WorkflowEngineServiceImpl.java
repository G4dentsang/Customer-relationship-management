package com.b2b.b2b.modules.workflow.service.impl;

import com.b2b.b2b.modules.crm.lead.entity.Lead;
import com.b2b.b2b.modules.workflow.entity.WorkflowAction;
import com.b2b.b2b.modules.workflow.entity.WorkflowRule;
import com.b2b.b2b.modules.workflow.listeners.WorkflowEngineListener;
import com.b2b.b2b.modules.workflow.service.WorkflowActionExecutorService;
import com.b2b.b2b.modules.workflow.service.WorkflowConditionEvaluatorService;
import com.b2b.b2b.modules.workflow.service.WorkflowEngineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkflowEngineServiceImpl implements WorkflowEngineService {

    Logger logger = LoggerFactory.getLogger(WorkflowEngineListener.class);

    private final WorkflowConditionEvaluatorService workflowConditionEvaluatorService;
    private final WorkflowActionExecutorService workflowActionExecutorService;

    public WorkflowEngineServiceImpl(WorkflowConditionEvaluatorService workflowConditionEvaluatorService,
                                     WorkflowActionExecutorService workflowActionExecutorService) {

        this.workflowConditionEvaluatorService = workflowConditionEvaluatorService;
        this.workflowActionExecutorService = workflowActionExecutorService;
    }
    @Override
    public void run(Lead lead, List<WorkflowRule> rules) {

            logger.info("rules found for lead id {}", lead.getId());
            for(WorkflowRule rule : rules){
                boolean match = workflowConditionEvaluatorService.evaluateCondition(rule.getWorkflowConditions(), lead);
                if(!match) break;
                logger.info("condition matched for lead's organization");
                for(WorkflowAction action: rule.getWorkflowActions()){
                    logger.info("running action for lead actions");
                    workflowActionExecutorService.execute(action, lead);
                }
            }
        }
}
