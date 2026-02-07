package com.b2b.b2b.modules.crm.pipelineStage.model;

import com.b2b.b2b.shared.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
public abstract class BasePipelineStage extends BaseEntity {

    @NotBlank(message = "Pipeline stage name is required")
    @Size(max = 100)
    @Column(name = "stage_name", nullable = false, length = 100)
    private String stageName;

    @Size(max = 255)
    @Column(name = "stage_desc",  length = 255)
    private String stageDescription;

    @NotNull(message = "Pipeline stage order is required")
    @Column(name = "stage_order", nullable = false)
    private Integer stageOrder;

    public BasePipelineStage(String stageName, String stageDescription, Integer stageOrder) {
        this.stageName = stageName;
        this.stageDescription = stageDescription;
        this.stageOrder = stageOrder;
    }

}
