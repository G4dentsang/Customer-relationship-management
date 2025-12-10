package com.b2b.b2b.modules.workflow.entity;

import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.workflow.enums.WorkflowTriggerType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WorkflowRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private WorkflowTriggerType workflowTriggerType;
    private String name;
    private String description;

    @ManyToOne
    private Organization organization;
    @OneToMany(mappedBy = "workflowRule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkflowCondition> workflowConditions = new ArrayList<>();
    @OneToMany(mappedBy = "workflowRule",  cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkflowAction> workflowActions = new ArrayList<>();

    private boolean isActive = true;
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }




}
