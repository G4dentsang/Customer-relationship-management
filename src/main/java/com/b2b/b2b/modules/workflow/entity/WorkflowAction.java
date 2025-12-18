package com.b2b.b2b.modules.workflow.entity;

import com.b2b.b2b.modules.workflow.enums.WorkflowActionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WorkflowAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private WorkflowActionType actionType;
    @Column(columnDefinition = "TEXT")
    private String actionConfigJson; //defines  actual action
    @ManyToOne(fetch = FetchType.LAZY)
    private WorkflowRule workflowRule;

    public WorkflowAction(WorkflowRule workflowRule, String actionConfigJson, WorkflowActionType actionType) {
        this.workflowRule = workflowRule;
        this.actionConfigJson = actionConfigJson;
        this.actionType = actionType;
    }
}
