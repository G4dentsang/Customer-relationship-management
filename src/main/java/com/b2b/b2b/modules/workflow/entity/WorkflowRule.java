package com.b2b.b2b.modules.workflow.entity;

import com.b2b.b2b.modules.workflow.enums.WorkflowTriggerType;
import com.b2b.b2b.shared.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WorkflowRule extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private WorkflowTriggerType workflowTriggerType;

    private String name;

    private String description;

    private boolean isActive;

    @OneToMany(mappedBy = "workflowRule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkflowCondition> workflowConditions = new ArrayList<>();

    @OneToMany(mappedBy = "workflowRule",  cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkflowAction> workflowActions = new ArrayList<>();

}
