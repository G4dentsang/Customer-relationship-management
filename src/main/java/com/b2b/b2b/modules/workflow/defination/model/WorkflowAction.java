package com.b2b.b2b.modules.workflow.defination.model;

import com.b2b.b2b.modules.workflow.enums.WorkflowActionType;
import com.b2b.b2b.shared.BaseEntity;
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
public class WorkflowAction extends BaseEntity {
    @Enumerated(EnumType.STRING)
    private WorkflowActionType actionType;

    @Column(columnDefinition = "TEXT")
    private String actionConfigJson; //actual action

    @ManyToOne(fetch = FetchType.LAZY)
    private WorkflowRule workflowRule;

}
