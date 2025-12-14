package com.b2b.b2b.modules.workflow.service.impl;

import com.b2b.b2b.modules.crm.lead.entity.Lead;
import com.b2b.b2b.modules.workflow.entity.WorkflowAction;
import com.b2b.b2b.modules.workflow.entity.WorkflowRule;
import com.b2b.b2b.modules.workflow.enums.WorkflowTriggerType;
import com.b2b.b2b.modules.workflow.listeners.WorkflowEngineListener;
import com.b2b.b2b.modules.workflow.service.WorkflowConditionEvaluatorService;
import com.b2b.b2b.modules.workflow.service.WorkflowEngineService;
import com.b2b.b2b.modules.workflow.service.WorkflowRuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkflowEngineServiceImpl implements WorkflowEngineService {
    Logger logger = LoggerFactory.getLogger(WorkflowEngineListener.class);
    private final WorkflowRuleService workflowRuleService;
    private final WorkflowConditionEvaluatorService workflowConditionEvaluatorService;
    public WorkflowEngineServiceImpl(WorkflowRuleService workflowRuleService, WorkflowConditionEvaluatorService workflowConditionEvaluatorService) {
        this.workflowRuleService = workflowRuleService;
        this.workflowConditionEvaluatorService = workflowConditionEvaluatorService;
    }
    @Override
    public void run(Lead lead) {
        Integer orgId = lead.getId();
        List<WorkflowRule> rules = workflowRuleService.getWorkflowRules(orgId, WorkflowTriggerType.LEAD_CREATED, true);
        if(rules.isEmpty()){
            logger.info("No rules found for lead id {}", orgId);
        }else{
            for(WorkflowRule rule : rules){
                boolean match = workflowConditionEvaluatorService.evaluateCondition(rule.getWorkflowConditions(), lead);
                if(match) continue;
                for(WorkflowAction action: rule.getWorkflowActions()){
                    //workflowActionExecutor.execute(action, lead);

                }
            }
        }


    }

    //create workflow condition for lead first
    //create workflow actions
    //then
    //fetch workflow rules  of org + the trigger
    //evaluate all conditions
    //based on condition execute workflow action
}
