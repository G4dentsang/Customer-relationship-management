package com.b2b.b2b.modules.crm.lead.controller;

import com.b2b.b2b.config.AppConstants;
import com.b2b.b2b.modules.crm.deal.payloads.DealResponseDTO;
import com.b2b.b2b.modules.crm.deal.service.DealService;
import com.b2b.b2b.modules.crm.lead.payloads.*;
import com.b2b.b2b.modules.crm.lead.service.LeadService;
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
    public ResponseEntity<Page<LeadResponseDTO>> listAll(LeadFilterDTO filter, @PageableDefault( size = AppConstants.DEFAULT_SIZE, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(leadService.findAllByOrganization(filter,pageable));
    }

    @PatchMapping("/{leadId}")
    public ResponseEntity<LeadResponseDTO> update(@PathVariable Integer leadId, @Valid @RequestBody UpdateLeadRequestDTO request) {
        return ResponseEntity.ok(leadService.update(leadId, request));
    }

    @PatchMapping("/{leadId}/status")
    public ResponseEntity<LeadResponseDTO> status(@PathVariable Integer leadId, @Valid @RequestBody LeadUpdateStatusRequestDTO request) {
        UpdateLeadRequestDTO mainDTO =  new UpdateLeadRequestDTO();
        mainDTO.setLeadStatus(request.getLeadStatus());
        return ResponseEntity.ok(leadService.update(leadId, mainDTO));
    }

    @PatchMapping("/{leadId}/assign")
    public ResponseEntity<LeadResponseDTO> assign(@PathVariable Integer leadId, @Valid @RequestBody AssignUserRequestDTO request) {
        UpdateLeadRequestDTO mainDTO =  new UpdateLeadRequestDTO();
        mainDTO.setOwner(request.getNewOwner());
        return ResponseEntity.ok(leadService.update(leadId, mainDTO));
    }

    @GetMapping("/my-leads") //userOwned
    public ResponseEntity<Page<LeadResponseDTO>> listMine(LeadFilterDTO filter, @PageableDefault(size = AppConstants.DEFAULT_SIZE, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(leadService.findMyList(filter, pageable));
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


}
