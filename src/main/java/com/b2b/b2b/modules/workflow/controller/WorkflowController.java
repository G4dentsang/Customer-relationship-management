package com.b2b.b2b.modules.workflow.controller;

import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.workflow.dto.WorkflowRuleCreateDTO;
import com.b2b.b2b.modules.workflow.dto.WorkflowRuleResponseDTO;
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
    //create
    private final AuthUtil authUtil;
    private final WorkflowRuleService workflowRuleService;
    public WorkflowController(WorkflowRuleService workflowRuleService, AuthUtil authUtil) {
        this.workflowRuleService = workflowRuleService;
        this.authUtil = authUtil;
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


}
