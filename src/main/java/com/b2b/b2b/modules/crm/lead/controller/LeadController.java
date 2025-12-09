package com.b2b.b2b.modules.crm.lead.controller;

import com.b2b.b2b.shared.MessageResponse;
import com.b2b.b2b.modules.crm.lead.payloads.LeadCreateDTO;
import com.b2b.b2b.modules.crm.lead.service.LeadService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app/v1/leads")
public class LeadController {

    private final LeadService leadService;

    public LeadController(LeadService leadService) {
        this.leadService = leadService;
    }

//    @PostMapping("/")
//    public ResponseEntity<?> createLead(@Valid @RequestBody LeadCreateDTO leadCreateDTO) {
//        leadService.createLead(leadCreateDTO);
//        return ResponseEntity.ok(new MessageResponse("Lead created successfully"));
//    }
//    @PatchMapping("/{leadId}")
//    public ResponseEntity<?> updateLead(@PathVariable Integer leadId,  @Valid @RequestBody LeadCreateDTO leadCreateDTO) {
//        leadService.updateLead(leadId, leadCreateDTO);
//        return ResponseEntity.ok(new MessageResponse("Lead updated successfully"));
//    }

}
