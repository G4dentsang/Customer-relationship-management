package com.b2b.b2b.modules.workflow.service.impl;

import com.b2b.b2b.exception.ResourceNotFoundException;
import com.b2b.b2b.exception.WorkflowMaintenanceException;
import com.b2b.b2b.modules.workflow.actions.WorkflowActionHandler;
import com.b2b.b2b.modules.workflow.entity.WorkflowAction;
import com.b2b.b2b.modules.workflow.entity.WorkflowRule;
import com.b2b.b2b.modules.workflow.enums.WorkflowActionType;
import com.b2b.b2b.modules.workflow.payloads.WorkflowActionDTO;
import com.b2b.b2b.modules.workflow.payloads.WorkflowActionResponseDTO;
import com.b2b.b2b.modules.workflow.repository.WorkflowActionRepository;
import com.b2b.b2b.modules.workflow.repository.WorkflowRuleRepository;
import com.b2b.b2b.modules.workflow.service.WorkflowActionService;
import com.b2b.b2b.modules.workflow.service.WorkflowTarget;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class WorkflowActionServiceImpl implements WorkflowActionService
{
    private final WorkflowRuleRepository workflowRuleRepository;
    private final ModelMapper modelMapper;
    private final Map<WorkflowActionType, WorkflowActionHandler> handlersMap = new HashMap<>();
    private final WorkflowActionRepository workflowActionRepository;
    private final Helpers helpers;

    public WorkflowActionServiceImpl(WorkflowRuleRepository workflowRuleRepository,
                                     ModelMapper modelMapper, List<WorkflowActionHandler> handlersList,
                                     WorkflowActionRepository workflowActionRepository, Helpers helpers) {
        this.workflowRuleRepository = workflowRuleRepository;
        this.modelMapper = modelMapper;
        for(WorkflowActionHandler handler : handlersList){
            handlersMap.put(handler.getType(), handler);
        }
        this.workflowActionRepository = workflowActionRepository;
        this.helpers = helpers;
    }

    @Override
    @Transactional
    public void execute(WorkflowAction action, WorkflowTarget target) {
        WorkflowActionHandler handler = handlersMap.get(action.getActionType());
        if (handler != null) {
            handler.handle(action, target);
        } else {
            log.error("No handler found for action type {}", action.getActionType());
        }
    }

    @Override
    public List<WorkflowActionResponseDTO> addActions(Integer id, List<WorkflowActionDTO> request) {
        WorkflowRule workflowRule = workflowRuleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("workflow", "Id", id));

        List<WorkflowAction> workflowActions = request.stream()
                .map(a -> {
                    WorkflowAction workflowAction = modelMapper.map(a, WorkflowAction.class);
                    workflowAction.setWorkflowRule(workflowRule);
                    return workflowAction;

                }).toList();

        return helpers.toDTOActionList(workflowActions);
    }

    @Override
    public void deleteAction(Integer ruleId, Long actionId) {
        WorkflowRule rule = workflowRuleRepository.findById(ruleId)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow", "id", ruleId));

        if(rule.isActive()) throw new WorkflowMaintenanceException("Rule must be inactive to delete logic.");

        WorkflowAction action = workflowActionRepository.findByIdAndWorkflowRule(actionId, rule)
                .orElseThrow(() -> new ResourceNotFoundException("WorkflowAction", "actionId", actionId));

        rule.getWorkflowActions().remove(action);
        workflowActionRepository.delete(action);
        workflowRuleRepository.save(rule);
        log.info("Action {} removed from Rule {} memory and DB", actionId, ruleId);

    }
}
