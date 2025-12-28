package com.b2b.b2b.modules.workflow.controller;

import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.workflow.payloads.*;
import com.b2b.b2b.modules.workflow.service.WorkflowActionService;
import com.b2b.b2b.modules.workflow.service.WorkflowConditionService;
import com.b2b.b2b.modules.workflow.service.WorkflowRuleService;
import com.b2b.b2b.shared.AuthUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("app/v1/workflows/rules")
@RequiredArgsConstructor
public class WorkflowController {

    private final AuthUtil authUtil;
    private final WorkflowRuleService workflowRuleService;
    private final WorkflowConditionService workflowConditionService;
    private final WorkflowActionService workflowActionService;

    @PostMapping
    public ResponseEntity<WorkflowRuleResponseDTO> create(@Valid @RequestBody WorkflowRuleCreateDTO request) {
        User user = authUtil.loggedInUser();
        return ResponseEntity.status(HttpStatus.CREATED).body(workflowRuleService.create(request, user));
    }

    @GetMapping
    public ResponseEntity<List<WorkflowRuleResponseDTO>> get() {
        User user = authUtil.loggedInUser();
        return ResponseEntity.ok(workflowRuleService.getAllWorkflowRules(user));
    }

    @GetMapping("/{ruleId}")
    public ResponseEntity<WorkflowRuleResponseDTO> getById(@PathVariable Integer ruleId) {
        User user = authUtil.loggedInUser();
        return ResponseEntity.ok(workflowRuleService.getWorkflowRule(ruleId, user));
    }

    @PostMapping("/{ruleId}/status")
    public ResponseEntity<WorkflowRuleResponseDTO> updateStatus(@PathVariable Integer ruleId, @RequestParam boolean status) {
        User user = authUtil.loggedInUser();
        return ResponseEntity.ok(workflowRuleService.updateStatus(ruleId, user, status));
    }

    @PostMapping("/{ruleId}/conditions")
    public ResponseEntity<List<WorkflowConditionResponseDTO>> addConditions(@PathVariable Integer ruleId, @RequestBody List<WorkflowConditionDTO> request) {
        User user = authUtil.loggedInUser();
        return ResponseEntity.status(HttpStatus.CREATED).body(workflowConditionService.addConditions(ruleId, request, user));
    }

    @PostMapping("/{ruleId}/actions")
    public ResponseEntity<List<WorkflowActionResponseDTO>> addActions(@PathVariable Integer ruleId, @RequestBody List<WorkflowActionDTO> request) {
        User user = authUtil.loggedInUser();
        return ResponseEntity.status(HttpStatus.CREATED).body(workflowActionService.addActions(ruleId, request, user));
    }


}
