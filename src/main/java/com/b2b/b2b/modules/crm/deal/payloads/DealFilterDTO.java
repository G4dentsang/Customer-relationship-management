package com.b2b.b2b.modules.crm.deal.payloads;

import com.b2b.b2b.modules.crm.deal.model.DealStatus;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class DealFilterDTO {
    @Size(max=100, message = "Search text is too long, be more precise")
    private String searchText;
    private DealStatus dealStatus;
    private Integer pipelineId;
    private Integer pipelineStageId;
    private Integer ownerId;

    @PositiveOrZero
    private Double minAmount;
    @PositiveOrZero
    private Double maxAmount;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate createdFrom;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate createdTo;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate closedFrom;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate closedTo;

    @AssertTrue(message = "Minimum amount cannot be greater than maximum amount")
    public boolean isAmountRangeValid() {
        if(minAmount == null || maxAmount == null) return true;
        return minAmount.compareTo(maxAmount) <= 0;
    }

    @AssertTrue(message = "Created-To date must be after Created-From date")
    public boolean isCreatedDateRangeValid() {
        if(createdFrom == null && createdTo == null) return true;
        return !createdTo.isBefore(createdFrom);
    }
}
