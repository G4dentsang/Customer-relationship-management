package com.b2b.b2b.modules.workflow.controller;

import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.workflow.dto.WorkflowRuleCreateDTO;
import com.b2b.b2b.modules.workflow.dto.WorkflowRuleResponseDTO;
import com.b2b.b2b.modules.workflow.service.WorkflowRuleService;
import com.b2b.b2b.shared.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("app/v1/workflows")
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
    //update
    //delete
    //read
}
