package com.b2b.b2b.modules.crm.pipeline.model;

import com.b2b.b2b.modules.organization.model.Organization;
import com.b2b.b2b.shared.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * every org 2 default pipeline
 * **/
@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
public abstract class BasePipeline extends BaseEntity {
    @NotBlank(message = "Pipeline name is required")
    @Size(max = 100)
    @Column(name = "pipline_name", nullable = false, length = 100)
    private String pipelineName;

    @Column(name = "is_default")
    private boolean isDefault;

    @Column(name = "is_Active")
    private boolean isActive = true;

    public BasePipeline(String pipelineName, boolean isDefault, Organization organization) {
        this.pipelineName = pipelineName;
        this.isDefault = isDefault;
        this.setOrganization(organization);
    }

}
