package com.b2b.b2b.modules.crm.pipeline.payloads;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
public class UpdatePipelineRequestDTO {
    @NotBlank(message = "Pipeline name is required")
    @Size(max = 100)
    private String pipelineName;

    @NotBlank(message = "default status of pipeline is required")
    private boolean isDefault;
}
