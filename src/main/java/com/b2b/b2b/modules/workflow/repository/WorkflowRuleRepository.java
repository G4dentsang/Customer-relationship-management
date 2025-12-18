package com.b2b.b2b.modules.workflow.repository;

import com.b2b.b2b.modules.workflow.entity.WorkflowRule;
import com.b2b.b2b.modules.workflow.enums.WorkflowTriggerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WorkflowRuleRepository extends JpaRepository<WorkflowRule, Integer>
{
    List<WorkflowRule> findByOrganization_organizationIdAndWorkflowTriggerTypeAndIsActive(
            Integer organizationId, WorkflowTriggerType  workflowTriggerType, Boolean isActive
    );
    @Query(
            "SELECT DISTINCT r FROM WorkflowRule r " +
            "LEFT JOIN FETCH r.workflowConditions " +
            "LEFT JOIN FETCH r.workflowActions " +
                    "WHERE r.organization.organizationId = :orgId AND r.workflowTriggerType = :trigger AND r.isActive = true"
    )
    List<WorkflowRule> findWorkflowRulesEagerly(
          @Param("orgId")  Integer organizationId,
          @Param("trigger")  WorkflowTriggerType  workflowTriggerType,
          @Param("active")  Boolean isActive
    );
}

