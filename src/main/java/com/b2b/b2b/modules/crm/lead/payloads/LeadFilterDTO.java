package com.b2b.b2b.modules.crm.lead.payloads;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
@Getter
@Setter
public class LeadFilterDTO {
    private String searchText;
    private String leadStatus;
    private Integer pipelineId;
    private Integer pipelineStageId;
    private Integer stageId;
    private Integer ownerId;

    private Boolean isConverted;


    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;
}
