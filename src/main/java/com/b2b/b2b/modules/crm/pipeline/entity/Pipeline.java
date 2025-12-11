package com.b2b.b2b.modules.crm.pipeline.entity;

import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.crm.lead.entity.Lead;
import com.b2b.b2b.modules.crm.pipelineStage.entity.PipelineStage;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Pipeline {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String pipelineName;
    private boolean isDefault;//every org 1 default pipeline
    private LocalDateTime createdAt;
    @ManyToOne
    private Organization organization;
    @OneToMany(mappedBy = "pipeline")
    private List<Lead> lead = new ArrayList<>();
    //deal later
    @OneToMany(mappedBy = "pipeline", cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<PipelineStage> pipelineStages = new ArrayList<>();

    public Pipeline(String pipelineName, boolean isDefault, LocalDateTime createdAt, Organization organization) {
        this.pipelineName = pipelineName;
        this.isDefault = isDefault;
        this.createdAt = createdAt;
        this.organization = organization;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
