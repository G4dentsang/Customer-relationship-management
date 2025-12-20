package com.b2b.b2b.modules.workflow.service.impl;

import com.b2b.b2b.modules.auth.repository.UserRepository;
import com.b2b.b2b.modules.workflow.entity.WorkflowAction;
import com.b2b.b2b.modules.workflow.listeners.WorkflowEngineListener;
import com.b2b.b2b.modules.workflow.service.WorkflowActionExecutorService;
import com.b2b.b2b.modules.workflow.service.WorkflowTarget;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkflowActionExecutorServiceImpl implements WorkflowActionExecutorService
{
    Logger logger = LoggerFactory.getLogger(WorkflowEngineListener.class);
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;


    public WorkflowActionExecutorServiceImpl(UserRepository userRepository, ObjectMapper objectMapper) {
        this.userRepository = userRepository;

        this.objectMapper = objectMapper;
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
}
