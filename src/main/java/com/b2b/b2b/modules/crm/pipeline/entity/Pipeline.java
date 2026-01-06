package com.b2b.b2b.modules.crm.pipeline.entity;

import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.crm.deal.entity.Deal;
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
    private Integer id;
    private String pipelineName;
    private boolean isDefault;
    private boolean isActive = true;
    private LocalDateTime createdAt;
    @Enumerated(EnumType.STRING)
    //every org 2 default pipeline
    private PipelineType pipelineType;
    @ManyToOne
    private Organization organization;
    @OneToMany(mappedBy = "pipeline")
    private List<Lead> lead = new ArrayList<>();
    @OneToMany(mappedBy = "pipeline")
    private List<Deal> deals = new ArrayList<>();
    @OneToMany(mappedBy = "pipeline", cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<PipelineStage> pipelineStages = new ArrayList<>();

    public Pipeline(String pipelineName, boolean isDefault, LocalDateTime createdAt,PipelineType pipelineType, Organization organization) {
        this.pipelineName = pipelineName;
        this.isDefault = isDefault;
        this.createdAt = createdAt;
        this.pipelineType = pipelineType;
        this.organization = organization;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
