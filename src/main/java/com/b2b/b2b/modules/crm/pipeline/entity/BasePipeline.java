package com.b2b.b2b.modules.crm.pipeline.entity;

import com.b2b.b2b.modules.auth.entity.Organization;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


/**
 * every org 2 default pipeline
 * **/
@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
public class BasePipeline {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    public BasePipeline(String pipelineName, boolean isDefault, Organization organization) {
        this.pipelineName = pipelineName;
        this.isDefault = isDefault;
        this.organization = organization;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
