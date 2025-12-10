package com.b2b.b2b.modules.crm.lead.controller;

import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.lead.payloads.LeadCreateDTO;
import com.b2b.b2b.modules.crm.lead.payloads.LeadResponseDTO;
import com.b2b.b2b.modules.crm.lead.service.LeadService;
import com.b2b.b2b.shared.AuthUtil;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app/v1/leads")
public class LeadController {
    Logger logger = LoggerFactory.getLogger(LeadController.class);
    private final LeadService leadService;
    private final AuthUtil authUtil;

    public LeadController(LeadService leadService, AuthUtil authUtil) {
        this.leadService = leadService;
        this.authUtil = authUtil;
    }

    @PostMapping("")
    public ResponseEntity<?> createLead(@Valid @RequestBody LeadCreateDTO leadCreateDTO) {
        User user = authUtil.loggedInUser();
        logger.info("Logged in USer : {}", user);
        LeadResponseDTO savedLeadResponseDTO = leadService.createLead(leadCreateDTO, user);
        return new ResponseEntity<>(savedLeadResponseDTO,HttpStatus.CREATED);
    }


}
