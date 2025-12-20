package com.b2b.b2b.modules.workflow.service.impl;

import com.b2b.b2b.modules.workflow.entity.WorkflowCondition;
import com.b2b.b2b.modules.workflow.listeners.WorkflowEngineListener;
import com.b2b.b2b.modules.workflow.service.WorkflowConditionEvaluatorService;
import com.b2b.b2b.modules.workflow.service.WorkflowTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.List;

@Service
public class WorkflowConditionEvaluatorServiceImpl implements WorkflowConditionEvaluatorService
{
    @Override
    public boolean evaluateCondition(List<WorkflowCondition> conditions, WorkflowTarget target) {
        Logger logger = LoggerFactory.getLogger(WorkflowEngineListener.class);
        for (WorkflowCondition workflowCondition : conditions) {
            String actualValue = getFieldValue(target, workflowCondition.getField());
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
    private String getFieldValue(WorkflowTarget target,String fieldName){
        try{
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return String.valueOf(field.get(target));

        }catch(Exception e){
            throw new RuntimeException("Invalid field " + fieldName + " in Lead");
        }
    }
}
