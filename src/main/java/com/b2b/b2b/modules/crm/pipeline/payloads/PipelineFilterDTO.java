package com.b2b.b2b.modules.crm.pipeline.payloads;

import com.b2b.b2b.modules.crm.pipeline.entity.PipelineType;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PipelineFilterDTO {
    @Size(max = 100, message = "Search text is too long ")
    private String searchText;

    private PipelineType pipelineType;
    private Boolean isActive;
    private Boolean isDefault;
}
