package com.b2b.b2b.modules.crm.lead.payloads;

import com.b2b.b2b.modules.crm.lead.entity.LeadStatus;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
@Getter
@Setter
public class LeadFilterDTO {
    @Size(max=100, message = "Search text is too long, be more precise")
    private String searchText;
    private LeadStatus leadStatus;
    private Integer pipelineId;
    private Integer pipelineStageId;
    private Integer ownerId;

    private Boolean isConverted;


    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    @AssertTrue(message = "End date must be after start date")
    public boolean isValidDateRage() {
        if (startDate == null || endDate == null) return true;
        return !endDate.isBefore(startDate);
    }
}
