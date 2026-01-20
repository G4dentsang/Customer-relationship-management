package com.b2b.b2b.modules.crm.lead.controller;

import com.b2b.b2b.modules.crm.deal.payloads.DealResponseDTO;
import com.b2b.b2b.modules.crm.deal.service.DealService;
import com.b2b.b2b.modules.crm.lead.payloads.*;
import com.b2b.b2b.modules.crm.lead.service.LeadService;
import com.b2b.b2b.shared.AuthUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/app/v1/leads")
@Slf4j
@RequiredArgsConstructor
public class LeadController {

    private final LeadService leadService;
    private final DealService dealService;
    private final AuthUtil authUtil;

    @PostMapping
    public ResponseEntity<LeadResponseDTO> create(@Valid @RequestBody CreateLeadRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(leadService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<LeadResponseDTO>> listAll() {
        return ResponseEntity.ok(leadService.findAllByOrganization());
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
    public ResponseEntity<List<LeadResponseDTO>> listMine() {
        return ResponseEntity.ok(leadService.findMyList());
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
