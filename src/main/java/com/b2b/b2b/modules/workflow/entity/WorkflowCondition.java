package com.b2b.b2b.modules.workflow.entity;

import com.b2b.b2b.modules.workflow.enums.WorkflowConditionOperator;
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
public class WorkflowCondition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private WorkflowConditionOperator workflowCondition;
    private String expectedValue;
    @ManyToOne
    private WorkflowRule workflowRule;

}
