package com.b2b.b2b.modules.workflow.entity;

import com.b2b.b2b.modules.workflow.enums.WorkflowConditionOperator;
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
public class WorkflowCondition extends BaseEntity {
    private String field;
    private WorkflowConditionOperator workflowConditionOperator;
    private String expectedValue;
    @ManyToOne
    private WorkflowRule workflowRule;

}
