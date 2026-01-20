package com.b2b.b2b.modules.workflow.service.impl;

import com.b2b.b2b.exception.ResourceNotFoundException;
import com.b2b.b2b.exception.WorkflowMaintenanceException;
import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.auth.repository.OrganizationRepository;
import com.b2b.b2b.modules.workflow.payloads.*;
import com.b2b.b2b.modules.workflow.entity.WorkflowRule;
import com.b2b.b2b.modules.workflow.enums.WorkflowTriggerType;
import com.b2b.b2b.modules.workflow.repository.WorkflowRuleRepository;
import com.b2b.b2b.modules.workflow.service.WorkflowRuleService;
import com.b2b.b2b.modules.workflow.util.WorkflowUtil;
import com.b2b.b2b.shared.AuthUtil;
import com.b2b.b2b.shared.multitenancy.OrganizationContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final OrganizationRepository organizationRepository;
    private final Helpers helpers;

    @Override
    public List<WorkflowRule> getActiveWorkflowRules(Organization org, WorkflowTriggerType type, Boolean isActive) {
        return workflowRuleRepository.findByOrganizationAndWorkflowTriggerTypeAndIsActive(org, type, isActive);
    }

    @Override
    public List<WorkflowRule> getAllRulesByTriggerType(Organization org, WorkflowTriggerType type) {
        return workflowRuleRepository.findByOrganizationAndWorkflowTriggerType(org, type);
    }

    @Override
    public List<WorkflowRuleResponseDTO> getAllWorkflowRules() {
        return helpers.toDTORuleList(workflowRuleRepository.findAll());
    }

    @Override
    public WorkflowRuleResponseDTO getWorkflowRule(Integer id) {
        WorkflowRule response = workflowRuleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow", "id", id));
        return workflowUtil.createWorkflowRuleResponseDTO(response);
    }

    @Override
    @Transactional
    public WorkflowRuleResponseDTO create(WorkflowRuleCreateDTO request) {
        Organization org = organizationRepository.findById(OrganizationContext.getOrgId())
                .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", OrganizationContext.getOrgId()));
        WorkflowRule rule = helpers.convertToEntity(request);

        rule.setOrganization(org);
        helpers.bindConditions(rule, request);
        helpers.bindActions(rule, request);

        return workflowUtil.createWorkflowRuleResponseDTO(workflowRuleRepository.save(rule));
    }

    @Override
    @Transactional
    public WorkflowRuleResponseDTO toggleStatus(Integer id, Boolean status) {
        User user = authUtil.loggedInUser();
        WorkflowRule rule = workflowRuleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow", "id", id));

        if (rule.isActive() == (status)) {
            return workflowUtil.createWorkflowRuleResponseDTO(rule);
        }
        rule.setActive(status);
        WorkflowRule updatedRule = workflowRuleRepository.save(rule);

        if (status) {
            //notificationService.broadcastToTeam(("Workflow " + rule.getName() + "' is now LIVE.");
            log.info("Workflow {} activated by {}", id, user.getEmail());
            log.info("notification is send");
        } else {
            //notificationService.broadcastToTeam(("Workflow " + rule.getName() + "' is currently DEACTIVATED.");
            log.info("Workflow {} placed in Maintenance Mode(DEACTIVATED) by {}", id, user.getEmail());
            log.info("notification is send");
        }

        return workflowUtil.createWorkflowRuleResponseDTO(updatedRule);
    }

    @Override
    public WorkflowRuleResponseDTO updateMetaData(Integer id, WorkflowRuleUpdateMetaDataDTO request) {
        WorkflowRule rule = workflowRuleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow", "id", id));
        rule.setName(request.getName());
        rule.setDescription(request.getDescription());
        return workflowUtil.createWorkflowRuleResponseDTO(workflowRuleRepository.save(rule));
    }

    @Override
    public WorkflowRuleResponseDTO updateLogic(Integer id, WorkflowRuleUpdateDTO request) {
        WorkflowRule rule = workflowRuleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow", "id", id));
        if (rule.isActive())
            throw new WorkflowMaintenanceException("Workflow is currently LIVE. You must deactivate it before saving logic changes.");

        rule.setWorkflowTriggerType(request.getWorkflowTriggerType());
        helpers.syncConditions(rule, request.getWorkflowConditions());
        helpers.syncActions(rule, request.getWorkflowActions());

        return workflowUtil.createWorkflowRuleResponseDTO(workflowRuleRepository.save(rule));
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        WorkflowRule rule = workflowRuleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow", "id", id));
        if (rule.isActive())
            throw new WorkflowMaintenanceException("Cannot delete an ACTIVE rule. Please deactivate it first.");
        workflowRuleRepository.delete(rule);
        //notificationService.broadcastToTeam(("Workflow " + rule.getName() + " is deleted by " + user.getEmail);
        log.info("Workflow Rule '{}' and all associated logic deleted by {}", rule.getName(), authUtil.loggedInUser().getEmail());
        log.info("notification is send"); //to remove later after postman

    }

}