package com.b2b.b2b.modules.crm.pipeline.entity;

import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.crm.deal.entity.Deal;
import com.b2b.b2b.modules.crm.lead.entity.Lead;
import com.b2b.b2b.modules.crm.pipelineStage.entity.PipelineStage;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FilterDef(
        name = "organizationFilter",
        parameters = @ParamDef(name = "orgId", type = Integer.class)
)
@Filter(
        name = "organizationFilter",
        condition = "organization_id = :orgId"
)
public class Pipeline {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pipeline_id", unique = true, nullable = false)
    private Integer id;

    @NotBlank(message = "Pipeline name is required")
    @Size(max = 100)
    @Column(name = "pipline_name", nullable = false, length = 100)
    private String pipelineName;

    @Column(name = "is_default")
    private boolean isDefault;

    @Column(name = "is_Active")
    private boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**every org 2 default pipeline**/
    @Enumerated(EnumType.STRING)
    @NotNull(message = "pipeline type is required for the pipeline")
    @Column(name = "pipeline_type",  nullable = false)
    private PipelineType pipelineType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
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
        this.updatedAt = LocalDateTime.now();
    }
}
