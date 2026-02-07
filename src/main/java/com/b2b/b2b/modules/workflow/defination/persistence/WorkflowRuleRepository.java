package com.b2b.b2b.modules.workflow.defination.persistence;

import com.b2b.b2b.modules.organization.model.Organization;
import com.b2b.b2b.modules.workflow.defination.model.WorkflowRule;
import com.b2b.b2b.modules.workflow.enums.WorkflowTriggerType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkflowRuleRepository extends JpaRepository<WorkflowRule, Integer> {
    List<WorkflowRule> findByOrganizationAndWorkflowTriggerTypeAndIsActive(Organization org, WorkflowTriggerType type, Boolean isActive);
    List<WorkflowRule> findByOrganizationAndWorkflowTriggerType(Organization org, WorkflowTriggerType type);
}

