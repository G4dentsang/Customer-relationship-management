package com.b2b.b2b.modules.crm.lead.api;

import com.b2b.b2b.config.AppConstants;
import com.b2b.b2b.modules.crm.deal.payloads.DealResponseDTO;
import com.b2b.b2b.modules.crm.deal.service.DealService;
import com.b2b.b2b.modules.crm.lead.payloads.*;
import com.b2b.b2b.modules.crm.lead.service.LeadService;
import com.b2b.b2b.shared.response.AppResponse;
import com.b2b.b2b.shared.response.PaginatedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/app/v1/leads")
@Slf4j
@RequiredArgsConstructor
public class LeadController {

    private final LeadService leadService;
    private final DealService dealService;

    @PostMapping
    public ResponseEntity<LeadResponseDTO> create(@Valid @RequestBody CreateLeadRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(leadService.create(request));
    }

    @GetMapping
    public ResponseEntity<AppResponse<PaginatedResponse<LeadResponseDTO>>> listAll(LeadFilterDTO filter, @PageableDefault(size = AppConstants.DEFAULT_SIZE, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<LeadResponseDTO> leadPage = leadService.findAllByOrganization(filter, pageable);
        PaginatedResponse<LeadResponseDTO> data = new PaginatedResponse<>(leadPage);
        return ResponseEntity.ok(new AppResponse<>(true, "Leads retrieved successfully", data));
    }

    @PatchMapping("/{leadId}")
    public ResponseEntity<LeadResponseDTO> update(@PathVariable Integer leadId, @Valid @RequestBody UpdateLeadRequestDTO request) {
        return ResponseEntity.ok(leadService.update(leadId, request));
    }

    @PatchMapping("/{leadId}/lost")
    public ResponseEntity<LeadResponseDTO> markAsLost(@PathVariable Integer leadId, @Valid @RequestBody LeadUpdateStatusRequestDTO request) {
        return ResponseEntity.ok(leadService.updateStatus(leadId, request));
    }

    @PatchMapping("/{leadId}/assign")
    public ResponseEntity<LeadResponseDTO> assign(@PathVariable Integer leadId, @Valid @RequestBody AssignUserRequestDTO request) {
        UpdateLeadRequestDTO mainDTO =  new UpdateLeadRequestDTO();
        mainDTO.setNewOwnerId(request.getNewOwnerId());
        return ResponseEntity.ok(leadService.update(leadId, mainDTO));
    }

    @GetMapping("/owner-leads")
    public ResponseEntity<AppResponse<PaginatedResponse<LeadResponseDTO>>> listMine(LeadFilterDTO filter, @PageableDefault(size = AppConstants.DEFAULT_SIZE, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<LeadResponseDTO> leadPage = leadService.findMyList(filter, pageable);
        PaginatedResponse<LeadResponseDTO> data = new PaginatedResponse<>(leadPage);
        return ResponseEntity.ok(new AppResponse<>(true, "leads retrieved successfully", data));
    }

    @GetMapping("/{leadId}")
    public ResponseEntity<LeadResponseDTO> get(@PathVariable Integer leadId) {
        return ResponseEntity.ok(leadService.getById(leadId));
    }

    @DeleteMapping("/{leadId}/delete")
    public ResponseEntity<Void> delete(@PathVariable Integer leadId) {
        leadService.delete(leadId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/{leadId}/convert")
    public ResponseEntity<DealResponseDTO> convert(@PathVariable Integer leadId) {
        return ResponseEntity.ok(dealService.convertFromLead(leadId));
    }

    @PutMapping("/{leadId}/stage")
    public ResponseEntity<LeadResponseDTO> updateStage(@PathVariable Integer leadId, @Valid @RequestBody ChangeStageRequestDTO request) {
        return ResponseEntity.ok(leadService.changeStage(leadId, request));
    }

}
