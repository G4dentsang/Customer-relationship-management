package com.b2b.b2b.modules.crm.pipelineStage.payloads;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PipelineStageFilterDTO {
    @Size(max = 100, message = "Search text is too long ")
    private String searchText;
    private Integer pipelineId;

}
