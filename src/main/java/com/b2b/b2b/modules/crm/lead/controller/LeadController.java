package com.b2b.b2b.modules.crm.lead.controller;

import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.deal.payloads.DealResponseDTO;
import com.b2b.b2b.modules.crm.deal.service.DealService;
import com.b2b.b2b.modules.crm.lead.payloads.CreateLeadRequestDTO;
import com.b2b.b2b.modules.crm.lead.payloads.LeadResponseDTO;
import com.b2b.b2b.modules.crm.lead.service.LeadService;
import com.b2b.b2b.shared.AuthUtil;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/app/v1/leads")
public class LeadController {
    Logger logger = LoggerFactory.getLogger(LeadController.class);
    private final LeadService leadService;
    private final DealService dealService;
    private final AuthUtil authUtil;

    public LeadController(LeadService leadService,DealService dealService, AuthUtil authUtil) {
        this.leadService = leadService;
        this.dealService = dealService;
        this.authUtil = authUtil;
    }

    @PostMapping("")
    public ResponseEntity<?> createLead(@Valid @RequestBody CreateLeadRequestDTO createLeadRequestDTO) {
        User user = authUtil.loggedInUser();
        LeadResponseDTO savedLeadResponseDTO = leadService.createLead(createLeadRequestDTO, user);
        return new ResponseEntity<>(savedLeadResponseDTO,HttpStatus.CREATED);
    }
    @GetMapping("")
    public ResponseEntity<?> getAllOrganizationLeads() {
        User user = authUtil.loggedInUser();
        List<LeadResponseDTO> leadResponseDTOs = leadService.getAllOrganizationLeads(user);
        return new  ResponseEntity<>(leadResponseDTOs,HttpStatus.OK);
    }
    @GetMapping("/userOwned")
    public ResponseEntity<?> getAllUserOwnedLeads() {
        User user = authUtil.loggedInUser();
        List<LeadResponseDTO> leadResponseDTOs = leadService.getAllUserOwnedLeads(user);
        return new  ResponseEntity<>(leadResponseDTOs,HttpStatus.OK);
    }
    @GetMapping("/{leadId}")
    public ResponseEntity<?> getLeadById(@PathVariable("leadId") Integer leadId) {
        User user = authUtil.loggedInUser();
        LeadResponseDTO leadResponseDTO = leadService.getLeadById(leadId, user);
        return new ResponseEntity<>(leadResponseDTO,HttpStatus.OK);
    }

    @PostMapping("/{leadId}/convert")
    public ResponseEntity<?> convertLead(@PathVariable("leadId") Integer leadId) {
        User user = authUtil.loggedInUser();
        DealResponseDTO dealResponseDTO = dealService.convertFromLead(leadId,user);
        return new ResponseEntity<>(dealResponseDTO,HttpStatus.OK);
    }


}
