package com.b2b.b2b.modules.workflow.entity;

import com.b2b.b2b.modules.workflow.enums.WorkflowConditionOperator;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FilterDef(
        name = "organizationFilter",
        parameters = @ParamDef(name = "orgId", type = Integer.class)
)
@Filter(
        name = "organizationFilter",
        condition = "organization_id = :orgId"
)
public class WorkflowCondition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String field;
    private WorkflowConditionOperator workflowConditionOperator;
    private String expectedValue;
    @ManyToOne
    private WorkflowRule workflowRule;

}
