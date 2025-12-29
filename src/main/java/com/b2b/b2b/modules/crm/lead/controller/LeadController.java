package com.b2b.b2b.modules.crm.lead.controller;

import com.b2b.b2b.modules.auth.entity.User;
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
        User user = authUtil.loggedInUser();
        return ResponseEntity.status(HttpStatus.CREATED).body(leadService.create(request, user));
    }

    @GetMapping
    public ResponseEntity<List<LeadResponseDTO>> listAll() {
        User user = authUtil.loggedInUser();
        return ResponseEntity.ok(leadService.findAllByOrganization(user));
    }

    @PatchMapping("/{leadId}")
    public ResponseEntity<LeadResponseDTO> update(@PathVariable Integer leadId, @Valid @RequestBody UpdateLeadRequestDTO request) {
        User user = authUtil.loggedInUser();
        return ResponseEntity.ok(leadService.update(leadId, request, user));
    }

    @PatchMapping("/{leadId}/status")
    public ResponseEntity<LeadResponseDTO> status(@PathVariable Integer leadId, @Valid @RequestBody UpdateStatusRequestDTO request) {
        User user = authUtil.loggedInUser();
        UpdateLeadRequestDTO mainDTO =  new UpdateLeadRequestDTO();
        mainDTO.setLeadStatus(request.getLeadStatus());
        return ResponseEntity.ok(leadService.update(leadId, mainDTO, user));
    }

    @PatchMapping("/{leadId}/assign")
    public ResponseEntity<LeadResponseDTO> assign(@PathVariable Integer leadId, @Valid @RequestBody AssignUserRequestDTO request) {
        User user = authUtil.loggedInUser();
        UpdateLeadRequestDTO mainDTO =  new UpdateLeadRequestDTO();
        mainDTO.setOwner(request.getNewOwner());
        return ResponseEntity.ok(leadService.update(leadId, mainDTO, user));
    }

    @GetMapping("/my-leads") //userOwned
    public ResponseEntity<List<LeadResponseDTO>> listMine() {
        User user = authUtil.loggedInUser();
        return ResponseEntity.ok(leadService.findAllByUser(user));
    }

    @GetMapping("/{leadId}")
    public ResponseEntity<LeadResponseDTO> get(@PathVariable Integer leadId) {
        User user = authUtil.loggedInUser();
        return ResponseEntity.ok(leadService.getById(leadId, user));
    }

    @DeleteMapping("/{leadId}/delete")
    public ResponseEntity<LeadResponseDTO> delete(@PathVariable Integer leadId) {
        User user = authUtil.loggedInUser();
        leadService.delete(leadId, user);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/{leadId}/convert")
    public ResponseEntity<DealResponseDTO> convert(@PathVariable Integer leadId) {
        User user = authUtil.loggedInUser();
        return ResponseEntity.ok(dealService.convertFromLead(leadId, user));
    }


}
