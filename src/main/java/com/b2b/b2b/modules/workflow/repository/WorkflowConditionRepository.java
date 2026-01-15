package com.b2b.b2b.modules.workflow.repository;

import com.b2b.b2b.modules.workflow.entity.WorkflowCondition;
import com.b2b.b2b.modules.workflow.entity.WorkflowRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkflowConditionRepository extends JpaRepository<WorkflowCondition, Integer>
{
   Optional<WorkflowCondition> findByIdAndWorkflowRule(Long id, WorkflowRule workflowRule);
}
