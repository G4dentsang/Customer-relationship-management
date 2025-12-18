package com.b2b.b2b.modules.workflow.service.impl;

import com.b2b.b2b.modules.crm.lead.entity.Lead;
import com.b2b.b2b.modules.workflow.entity.WorkflowCondition;
import com.b2b.b2b.modules.workflow.listeners.WorkflowEngineListener;
import com.b2b.b2b.modules.workflow.service.WorkflowConditionEvaluatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.List;

@Service
public class WorkflowConditionEvaluatorServiceImpl implements WorkflowConditionEvaluatorService
{
    @Override
    public boolean evaluateCondition(List<WorkflowCondition> conditions, Lead lead) {
        Logger logger = LoggerFactory.getLogger(WorkflowEngineListener.class);
        for (WorkflowCondition workflowCondition : conditions) {
            logger.info("existing conditions: {}", workflowCondition);
            String actualValue = getFieldValue(lead, workflowCondition.getField());
            String expectedValue = workflowCondition.getExpectedValue();
            switch(workflowCondition.getWorkflowConditionOperator()){
                case EQUALS:
                    if(!expectedValue.equals(actualValue)) return  false;
                    break;
                    case NOT_EQUALS:
                        if(expectedValue.equals(actualValue)) return  false;
                        break;
                        //more operators
            }
        }
        return true;
    }
    private String getFieldValue(Lead lead,String fieldName){
        try{
            Field field = lead.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return String.valueOf(field.get(lead));

        }catch(Exception e){
            throw new RuntimeException("Invalid field " + fieldName + " in Lead");
        }
    }
}
