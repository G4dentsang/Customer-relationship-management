package com.b2b.b2b.modules.workflow.repository;

import com.b2b.b2b.modules.workflow.entity.WorkflowAction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkflowActionRepository extends JpaRepository<WorkflowAction, Integer> {
}
