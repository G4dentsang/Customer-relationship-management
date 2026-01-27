package com.b2b.b2b.modules.crm.pipeline.payloads;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PipelineMigrationRequestDTO {
    @NotBlank(message = "target pipeline is required")
    Integer targetPipelineId;

    @NotBlank(message = "target pipeline stage is required")
    Integer targetStageId;
}
