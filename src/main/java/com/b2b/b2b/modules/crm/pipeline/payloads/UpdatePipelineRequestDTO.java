package com.b2b.b2b.modules.crm.pipeline.payloads;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdatePipelineRequestDTO {
    private String pipelineName;
    private boolean isDefault;
}
