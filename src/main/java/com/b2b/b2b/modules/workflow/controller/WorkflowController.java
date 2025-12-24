package com.b2b.b2b.modules.workflow.controller;

import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.workflow.payloads.*;
import com.b2b.b2b.modules.workflow.repository.WorkflowConditionRepository;
import com.b2b.b2b.modules.workflow.service.WorkflowActionService;
import com.b2b.b2b.modules.workflow.service.WorkflowConditionService;
import com.b2b.b2b.modules.workflow.service.WorkflowRuleService;
import com.b2b.b2b.shared.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("app/v1/workflows/rules")
public class WorkflowController {

    private final AuthUtil authUtil;
    private final WorkflowRuleService workflowRuleService;
    private final WorkflowConditionService workflowConditionService;
    private final WorkflowActionService workflowActionService;

    public WorkflowController(WorkflowRuleService workflowRuleService, AuthUtil authUtil, WorkflowConditionService workflowConditionService, WorkflowActionService workflowActionService) {
        this.workflowRuleService = workflowRuleService;
        this.authUtil = authUtil;
        this.workflowConditionService = workflowConditionService;
        this.workflowActionService = workflowActionService;
    }
    @PostMapping("")
    public ResponseEntity<?> createWorkflowRule(@Valid @RequestBody WorkflowRuleCreateDTO workflowRuleCreateDTO)
    {   User user = authUtil.loggedInUser();
        WorkflowRuleResponseDTO workflowRuleResponseDTO = workflowRuleService.saveWorkflowRule(workflowRuleCreateDTO, user);
        return new ResponseEntity<>(workflowRuleResponseDTO,HttpStatus.CREATED);
    }
    @GetMapping("")
    public ResponseEntity<?> getWorkflowRules() {
        User  user = authUtil.loggedInUser();
        List<WorkflowRuleResponseDTO> workflowRuleResponseDTOs = workflowRuleService.getAllWorkflowRules(user);
        return  new ResponseEntity<>(workflowRuleResponseDTOs,HttpStatus.OK);
    }
    @GetMapping("/{ruleId}")
    public ResponseEntity<?> getWorkflowRule(@PathVariable("ruleId") Integer ruleId) {
        User  user = authUtil.loggedInUser();
        WorkflowRuleResponseDTO workflowRuleResponseDTO = workflowRuleService.getWorkflowRule(ruleId, user);
        return  new ResponseEntity<>(workflowRuleResponseDTO,HttpStatus.OK);
    }
    @PostMapping("/{ruleId}/activate")
    public ResponseEntity<?> activateWorkflowRule(@PathVariable("ruleId") Integer ruleId) {
        User  user = authUtil.loggedInUser();
        WorkflowRuleResponseDTO workflowRuleResponseDTO = workflowRuleService.activateWorkflowRule(ruleId, user);
        return new ResponseEntity<>(workflowRuleResponseDTO,HttpStatus.OK);
    }
    @PostMapping("/{ruleId}/deactivate")
    public ResponseEntity<?> deactivateWorkflowRule(@PathVariable("ruleId") Integer ruleId) {
        User  user = authUtil.loggedInUser();
        WorkflowRuleResponseDTO workflowRuleResponseDTO = workflowRuleService.deactivateWorkflowRule(ruleId, user);
        return new ResponseEntity<>(workflowRuleResponseDTO,HttpStatus.OK);
    }
   @PostMapping("/{ruleId}/conditions")
    public ResponseEntity<?> addWorkflowConditions( @PathVariable("ruleId") Integer ruleId, @RequestBody List<WorkflowConditionDTO>  workflowConditionDTOs) {
        User  user = authUtil.loggedInUser();
       List<WorkflowConditionResponseDTO> workflowConditionResponseDTOs = workflowConditionService.addWorkflowConditions(ruleId,workflowConditionDTOs,user);
       return new ResponseEntity<>(workflowConditionResponseDTOs,HttpStatus.CREATED);
   }
   @PostMapping("/{ruleId}/actions")
   public ResponseEntity<?> addWorkflowActions(@PathVariable("ruleId") Integer ruleId, @RequestBody List<WorkflowActionDTO>  workflowActionDTOs) {
       User  user = authUtil.loggedInUser();
       List<WorkflowActionResponseDTO> workflowActionResponseDTOs = workflowActionService.addWorkflowActions(ruleId,workflowActionDTOs,user);
       return new ResponseEntity<>(workflowActionResponseDTOs,HttpStatus.CREATED);
   }


}
