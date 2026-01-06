package com.b2b.b2b.modules.crm.pipeline.payloads;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PipelineMigrationRequestDTO {
    Integer targetPipelineId;
    Integer targetStageId;
}
