package com.b2b.b2b.modules.crm.deal.payloads;

import com.b2b.b2b.modules.crm.deal.entity.DealStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class DealFilterDTO {
    private String searchText;
    private DealStatus dealstatus;
    private Integer pipelineId;
    private Integer pipelineStageId;
    private Integer ownerId;

    private Double minAmount;
    private Double maxAmount;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate createdFrom;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate createdTo;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate closedFrom;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate closedTo;
}
