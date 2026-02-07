package com.b2b.b2b.modules.crm.pipeline.payloads;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PipelineMigrationRequestDTO {
    @NotBlank(message = "target pipeline is required")
    Integer targetPipelineId;
    @NotEmpty(message = "You must provide mapping for the stages.")
    private Map<Integer, Integer> stageMapping;
}
