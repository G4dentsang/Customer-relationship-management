package com.b2b.b2b.modules.workflow.controller;

import com.b2b.b2b.config.AppConstants;
import com.b2b.b2b.modules.workflow.payloads.*;
import com.b2b.b2b.modules.workflow.service.WorkflowActionService;
import com.b2b.b2b.modules.workflow.service.WorkflowConditionService;
import com.b2b.b2b.modules.workflow.service.WorkflowRuleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("app/v1/workflows/rules")
@RequiredArgsConstructor
public class WorkflowController {

    private final WorkflowRuleService workflowRuleService;
    private final WorkflowConditionService workflowConditionService;
    private final WorkflowActionService workflowActionService;

    @PostMapping
    public ResponseEntity<WorkflowRuleResponseDTO> create(@Valid @RequestBody WorkflowRuleCreateDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(workflowRuleService.create(request));
    }

    @GetMapping
    public ResponseEntity<Page<WorkflowRuleResponseDTO>> listAll(@PageableDefault(size = AppConstants.DEFAULT_SIZE, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(workflowRuleService.getAllWorkflowRules(pageable));
    }

    @GetMapping("/{ruleId}")
    public ResponseEntity<WorkflowRuleResponseDTO> get(@PathVariable Integer ruleId) {
        return ResponseEntity.ok(workflowRuleService.getWorkflowRule(ruleId));
    }

    @PostMapping("/{ruleId}/status")
    public ResponseEntity<WorkflowRuleResponseDTO> activate(@PathVariable Integer ruleId, @RequestParam boolean status) {
        return ResponseEntity.ok(workflowRuleService.toggleStatus(ruleId, status));
    }

    @PatchMapping("/{ruleId}/metadata")
    public ResponseEntity<WorkflowRuleResponseDTO> updateMetaData(@PathVariable Integer ruleId, @RequestBody WorkflowRuleUpdateMetaDataDTO request) {
        return ResponseEntity.ok(workflowRuleService.updateMetaData(ruleId, request));
    }

    @PutMapping("/{ruleId}/logic")
    public ResponseEntity<WorkflowRuleResponseDTO> updateLogic(@PathVariable Integer ruleId, @RequestBody WorkflowRuleUpdateDTO request) {
        return ResponseEntity.ok(workflowRuleService.updateLogic(ruleId, request));
    }

    @DeleteMapping("{/ruleId}")
    public ResponseEntity<Void> delete(@PathVariable Integer ruleId) {
        workflowRuleService.delete(ruleId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build() ;
    }

    @PostMapping("/{ruleId}/conditions")
    public ResponseEntity<List<WorkflowConditionResponseDTO>> addConditions(@PathVariable Integer ruleId, @RequestBody List<WorkflowConditionDTO> request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(workflowConditionService.addConditions(ruleId, request));
    }

    @DeleteMapping("/{ruleId}/conditions/{conditionId}")
    public ResponseEntity<Void> deleteCondition(@PathVariable Integer ruleId, @PathVariable Long conditionId) {
        workflowConditionService.deleteCondition(ruleId, conditionId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build() ;
    }

    @PostMapping("/{ruleId}/actions")
    public ResponseEntity<List<WorkflowActionResponseDTO>> addActions(@PathVariable Integer ruleId, @RequestBody List<WorkflowActionDTO> request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(workflowActionService.addActions(ruleId, request));
    }

    @DeleteMapping("/{ruleId}/actions/{actionId}")
    public ResponseEntity<Void> deleteAction(@PathVariable Integer ruleId, @PathVariable Long actionId) {
        workflowActionService.deleteAction(ruleId, actionId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build() ;
    }

}
