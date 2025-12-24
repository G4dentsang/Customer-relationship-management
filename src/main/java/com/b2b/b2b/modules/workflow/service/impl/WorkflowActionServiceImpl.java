package com.b2b.b2b.modules.workflow.service.impl;

import com.b2b.b2b.exception.APIException;
import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.auth.repository.UserRepository;
import com.b2b.b2b.modules.workflow.entity.WorkflowAction;
import com.b2b.b2b.modules.workflow.entity.WorkflowRule;
import com.b2b.b2b.modules.workflow.listeners.WorkflowEngineListener;
import com.b2b.b2b.modules.workflow.payloads.WorkflowActionDTO;
import com.b2b.b2b.modules.workflow.payloads.WorkflowActionResponseDTO;
import com.b2b.b2b.modules.workflow.repository.WorkflowActionRepository;
import com.b2b.b2b.modules.workflow.repository.WorkflowRuleRepository;
import com.b2b.b2b.modules.workflow.service.WorkflowActionService;
import com.b2b.b2b.modules.workflow.service.WorkflowTarget;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class WorkflowActionServiceImpl implements WorkflowActionService
{
    private final WorkflowRuleRepository workflowRuleRepository;
    private final WorkflowActionRepository workflowActionRepository;
    Logger logger = LoggerFactory.getLogger(WorkflowEngineListener.class);
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;


    public WorkflowActionServiceImpl(UserRepository userRepository, ObjectMapper objectMapper, WorkflowRuleRepository workflowRuleRepository, WorkflowActionRepository workflowActionRepository) {
        this.userRepository = userRepository;

        this.objectMapper = objectMapper;
        this.workflowRuleRepository = workflowRuleRepository;
        this.workflowActionRepository = workflowActionRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public void execute(WorkflowAction action, WorkflowTarget target) {
        switch (action.getActionType()){
            case SEND_EMAIL:
                executeEmailAction(action, target);
                break;
            case ASSIGN_USER:
                executeAssignmentAction(action,target);
                //many more cases will to add later ******
        }
    }
//later create a separate class containing all the below method inside  Util package of workflow module ****************

    @Async
    @Override
    public void executeEmailAction(WorkflowAction action, WorkflowTarget target) {
        logger.info("Sending email to target");
        try{
            JsonNode node = objectMapper.readTree(action.getActionConfigJson());
            Integer templateId = node.path("templateId").asInt();
            String to = node.path("to").asText();
            //   String from = node.get("from").asText();
            //  String subject = node.get("subject").asText();
            //  String body = node.get("body").asText();
            //call the email service here later 1
            //you might add notification feature later 5
        }catch(Exception e){
           throw new RuntimeException(e.getMessage());
        }

    }
    @Async
    @Override
    public void executeAssignmentAction(WorkflowAction action, WorkflowTarget target) {
        logger.info("Assigning user to target");
        //add after user with role not admin  creation
    /* try{

        JsonNode node = objectMapper.readTree(action.getActionConfigJson());
        Integer assignedUserId = node.get("assignedUserId").asInt();
        User user = userRepository.findById(assignedUserId).orElseThrow(()->
                 new APIException("Assigned user is not found "));
        target.setOwner(user);

     }catch(Exception e){
         throw new RuntimeException(e.getMessage());
     }*/
    }

    @Override
    public List<WorkflowActionResponseDTO> addWorkflowActions(Integer ruleId, List<WorkflowActionDTO> workflowActionDTOs, User user) {
        Organization organization = user.getUserOrganizations()
                .stream()
                .filter(userOrg -> userOrg.isPrimary())
                .findFirst()
                .orElseThrow(()-> new APIException("User's organization not found"))
                .getOrganization();
        WorkflowRule workflowRule = workflowRuleRepository.findByIdAndOrganization(ruleId, organization);
        List<WorkflowAction> workflowActions = new ArrayList<>();
        for(WorkflowActionDTO workflowActionDTO : workflowActionDTOs){
            WorkflowAction workflowAction = new WorkflowAction();
            workflowAction.setActionConfigJson(workflowActionDTO.getActionConfigJson());
            workflowAction.setWorkflowRule(workflowRule);
            workflowAction.setActionType(workflowActionDTO.getWorkflowActionType());

            workflowActionRepository.save(workflowAction);
            workflowActions.add(workflowAction);
        }
        return workflowActions.stream().map(action -> new WorkflowActionResponseDTO(
                action.getActionType(),
                action.getActionConfigJson()
        )
        ).toList();
    }
}
