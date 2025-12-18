package com.b2b.b2b.modules.workflow.service.impl;

import com.b2b.b2b.modules.auth.repository.UserRepository;
import com.b2b.b2b.modules.crm.lead.entity.Lead;
import com.b2b.b2b.modules.crm.lead.repository.LeadRepository;
import com.b2b.b2b.modules.workflow.entity.WorkflowAction;
import com.b2b.b2b.modules.workflow.listeners.WorkflowEngineListener;
import com.b2b.b2b.modules.workflow.service.WorkflowActionExecutorService;
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
    private final LeadRepository leadRepository;
    private final ObjectMapper objectMapper;


    public WorkflowActionExecutorServiceImpl(UserRepository userRepository, LeadRepository leadRepository, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.leadRepository = leadRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    @Override
    public void execute(WorkflowAction action, Lead lead) {
        switch (action.getActionType()){
            case SEND_EMAIL:
                executeEmailAction(action, lead);
                break;
            case ASSIGN_USER:
                executeAssignmentAction(action,lead);
                //many more cases will to add later ******
        }
    }
//later create a separate class containing all the below method inside  Util package of workflow module ****************

    @Async
    @Override
    public void executeEmailAction(WorkflowAction action, Lead lead) {
        logger.info("Sending email to lead");
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
    public void executeAssignmentAction(WorkflowAction action, Lead lead) {
        logger.info("Assigning user to lead");
        //add after user with role not admin  creation
    /* try{

        JsonNode node = objectMapper.readTree(action.getActionConfigJson());
        Integer assignedUserId = node.get("assignedUserId").asInt();
        User user = userRepository.findById(assignedUserId).orElseThrow(()->
                 new APIException("Assigned user is not found "));
        lead.setOwner(user);
        logger.info("This Lead: {},  is assigned to user {}.",lead.getLeadName(), user.getUserName());
        leadRepository.save(lead);

     }catch(Exception e){
         throw new RuntimeException(e.getMessage());
     }*/
    }
}
