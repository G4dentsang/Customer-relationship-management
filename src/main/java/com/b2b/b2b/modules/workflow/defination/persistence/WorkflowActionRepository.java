package com.b2b.b2b.modules.workflow.defination.persistence;

import com.b2b.b2b.modules.workflow.defination.model.WorkflowAction;
import com.b2b.b2b.modules.workflow.defination.model.WorkflowRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkflowActionRepository extends JpaRepository<WorkflowAction, Integer> {


    Optional<WorkflowAction> findByIdAndWorkflowRule(Long actionId, WorkflowRule workflowRule);
}
