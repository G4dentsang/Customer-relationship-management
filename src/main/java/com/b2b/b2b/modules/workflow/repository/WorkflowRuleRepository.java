package com.b2b.b2b.modules.workflow.repository;

import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.workflow.entity.WorkflowRule;
import com.b2b.b2b.modules.workflow.enums.WorkflowTriggerType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkflowRuleRepository extends JpaRepository<WorkflowRule, Integer>
{
      List<WorkflowRule> findByOrganizationAndWorkflowTriggerTypeAndIsActive(
            Organization org, WorkflowTriggerType  type, Boolean isActive
      );
      List<WorkflowRule> findByOrganizationAndWorkflowTriggerType(Organization org, WorkflowTriggerType type);
      List<WorkflowRule> findAllByOrganization(Organization org);
      Optional<WorkflowRule>  findByIdAndOrganization(Integer id, Organization org);
}

