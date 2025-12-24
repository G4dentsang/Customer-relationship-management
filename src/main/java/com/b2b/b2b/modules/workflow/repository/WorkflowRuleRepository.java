package com.b2b.b2b.modules.workflow.repository;

import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.workflow.entity.WorkflowRule;
import com.b2b.b2b.modules.workflow.enums.WorkflowTriggerType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkflowRuleRepository extends JpaRepository<WorkflowRule, Integer>
{
      List<WorkflowRule> findByOrganization_organizationIdAndWorkflowTriggerTypeAndIsActive(
            Integer organizationId, WorkflowTriggerType  workflowTriggerType, Boolean isActive
      );
      List<WorkflowRule> findAllByOrganization(Organization organization);
      WorkflowRule  findByIdAndOrganization(Integer id, Organization organization);
}

