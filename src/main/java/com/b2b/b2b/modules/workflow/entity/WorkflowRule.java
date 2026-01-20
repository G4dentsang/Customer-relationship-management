package com.b2b.b2b.modules.workflow.entity;

import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.workflow.enums.WorkflowTriggerType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
public class WorkflowRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private WorkflowTriggerType workflowTriggerType;

    private String name;
    private String description;
    private boolean isActive;
    private LocalDateTime createdAt;

    @ManyToOne
    private Organization organization;
    @OneToMany(mappedBy = "workflowRule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkflowCondition> workflowConditions = new ArrayList<>();
    @OneToMany(mappedBy = "workflowRule",  cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkflowAction> workflowActions = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }




}
