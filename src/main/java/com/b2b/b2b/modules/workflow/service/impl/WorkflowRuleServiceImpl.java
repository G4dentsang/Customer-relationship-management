package com.b2b.b2b.modules.workflow.service.impl;

import com.b2b.b2b.exception.ResourceNotFoundException;
import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.workflow.payloads.WorkflowRuleCreateDTO;
import com.b2b.b2b.modules.workflow.payloads.WorkflowRuleResponseDTO;
import com.b2b.b2b.modules.workflow.entity.WorkflowAction;
import com.b2b.b2b.modules.workflow.entity.WorkflowCondition;
import com.b2b.b2b.modules.workflow.entity.WorkflowRule;
import com.b2b.b2b.modules.workflow.enums.WorkflowTriggerType;
import com.b2b.b2b.modules.workflow.repository.WorkflowRuleRepository;
import com.b2b.b2b.modules.workflow.service.WorkflowRuleService;
import com.b2b.b2b.modules.workflow.util.WorkflowUtil;
import com.b2b.b2b.shared.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowRuleServiceImpl implements WorkflowRuleService {

    private final WorkflowRuleRepository workflowRuleRepository;
    private final WorkflowUtil workflowUtil;
    private final AuthUtil authUtil;
    private final ModelMapper modelMapper;

    @Override
    public List<WorkflowRule> getWorkflowRules(Organization org, WorkflowTriggerType type, Boolean isActive) {
        return workflowRuleRepository.findByOrganizationAndWorkflowTriggerTypeAndIsActive(org, type, isActive);
    }

    @Override
    public List<WorkflowRuleResponseDTO> getAllWorkflowRules(User user) {
        return toDTOList(workflowRuleRepository.findAllByOrganization(getOrg(user)));
    }

    @Override
    public WorkflowRuleResponseDTO getWorkflowRule(Integer id, User user) {
        WorkflowRule response = workflowRuleRepository.findByIdAndOrganization(id, getOrg(user))
                .orElseThrow(() -> new ResourceNotFoundException("Workflow", "id", id));
        return workflowUtil.createWorkflowRuleResponseDTO(response);
    }

    @Override
    @Transactional
    public WorkflowRuleResponseDTO create(WorkflowRuleCreateDTO request, User user) {
        WorkflowRule rule = convertToEntity(request);

        rule.setOrganization(getOrg(user));
        bindConditions(rule, request);
        bindActions(rule, request);

        return workflowUtil.createWorkflowRuleResponseDTO(workflowRuleRepository.save(rule));
    }

    @Override
    @Transactional
    public WorkflowRuleResponseDTO updateStatus(Integer id, User user, Boolean status) {
        WorkflowRule rule = workflowRuleRepository.findByIdAndOrganization(id, getOrg(user))
                .orElseThrow(() -> new ResourceNotFoundException("Workflow", "id", id));

        if (rule.isActive() == status) {
            return workflowUtil.createWorkflowRuleResponseDTO(rule);
        }
        rule.setActive(status);

        WorkflowRule updatedRule = workflowRuleRepository.save(rule);
        log.info("Workflow {} status updated to {} by user {}", id, status, user.getEmail());

        return workflowUtil.createWorkflowRuleResponseDTO(updatedRule);
    }

    /******************Helper methods************************/

    private Organization getOrg(User user) {
        return authUtil.getPrimaryOrganization(user);
    }

    private List<WorkflowRuleResponseDTO> toDTOList(List<WorkflowRule> rules) {
        return rules.stream().map(workflowUtil::createWorkflowRuleResponseDTO).toList();
    }

    private WorkflowRule convertToEntity(WorkflowRuleCreateDTO request) {
        WorkflowRule workflowRule = new WorkflowRule();
        workflowRule.setName(request.getName());
        workflowRule.setDescription(request.getDescription());
        workflowRule.setWorkflowTriggerType(request.getWorkflowTriggerType());
        workflowRule.setActive(request.isActive());
        return workflowRule;
    }

    private void bindConditions(WorkflowRule rule, WorkflowRuleCreateDTO request) {
        List<WorkflowCondition> conditions = request.getWorkflowConditions()
                .stream()
                .map(c -> {
                    WorkflowCondition condition = modelMapper.map(c, WorkflowCondition.class);
                    condition.setWorkflowRule(rule);
                    return condition;
                }).toList();
        rule.setWorkflowConditions(conditions);
    }

    private void bindActions(WorkflowRule rule, WorkflowRuleCreateDTO request) {
        List<WorkflowAction> actions = request.getWorkflowActions()
                .stream()
                .map(a -> {
                    WorkflowAction workflowAction = modelMapper.map(a, WorkflowAction.class);
                    workflowAction.setWorkflowRule(rule);
                    return workflowAction;
                }).toList();
        rule.setWorkflowActions(actions);
    }

}