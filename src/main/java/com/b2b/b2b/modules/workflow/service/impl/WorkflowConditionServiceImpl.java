package com.b2b.b2b.modules.workflow.service.impl;

import com.b2b.b2b.exception.APIException;
import com.b2b.b2b.exception.ResourceNotFoundException;
import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.workflow.entity.WorkflowCondition;
import com.b2b.b2b.modules.workflow.entity.WorkflowRule;
import com.b2b.b2b.modules.workflow.listeners.WorkflowEngineListener;
import com.b2b.b2b.modules.workflow.payloads.WorkflowConditionDTO;
import com.b2b.b2b.modules.workflow.payloads.WorkflowConditionResponseDTO;
import com.b2b.b2b.modules.workflow.repository.WorkflowConditionRepository;
import com.b2b.b2b.modules.workflow.repository.WorkflowRuleRepository;
import com.b2b.b2b.modules.workflow.service.WorkflowConditionService;
import com.b2b.b2b.modules.workflow.service.WorkflowTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Service
public class WorkflowConditionServiceImpl implements WorkflowConditionService
{
    private final WorkflowRuleRepository workflowRuleRepository;
    private final WorkflowConditionRepository workflowConditionRepository;

    public WorkflowConditionServiceImpl(WorkflowRuleRepository workflowRuleRepository, WorkflowConditionRepository workflowConditionRepository) {
        this.workflowRuleRepository = workflowRuleRepository;
        this.workflowConditionRepository = workflowConditionRepository;
    }

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

    @Override
    public List<WorkflowConditionResponseDTO> addWorkflowConditions(Integer ruleId, List<WorkflowConditionDTO> conditions, User user) {
        Organization organization = user.getUserOrganizations()
                .stream()
                .filter(userOrg -> userOrg.isPrimary())
                .findFirst()
                .orElseThrow(() -> new APIException("User's organization not found"))
                .getOrganization();
        WorkflowRule rule = workflowRuleRepository.findByIdAndOrganization(ruleId,organization);

        List<WorkflowCondition> workflowConditions = new ArrayList<>();

        for(WorkflowConditionDTO workflowConditionDTO : conditions){
            WorkflowCondition workflowCondition = new WorkflowCondition(
                    workflowConditionDTO.getExpectedValue(),
                    workflowConditionDTO.getWorkflowConditionOperator(),
                    workflowConditionDTO.getField(),
                    rule
            );
            workflowConditionRepository.save(workflowCondition);
            workflowConditions.add(workflowCondition);
        }

        return workflowConditions.stream().map(condition -> new WorkflowConditionResponseDTO(
                condition.getField(),
                condition.getWorkflowConditionOperator(),
                condition.getExpectedValue()
                )
        ).toList();
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
