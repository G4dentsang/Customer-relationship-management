package com.b2b.b2b.modules.workflow.repository;

import com.b2b.b2b.modules.workflow.entity.WorkflowCondition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkflowConditionRepository extends JpaRepository<WorkflowCondition, Integer>
{
}
