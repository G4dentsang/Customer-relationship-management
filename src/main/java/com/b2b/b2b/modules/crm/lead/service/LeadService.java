package com.b2b.b2b.modules.crm.lead.service;

import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.lead.payloads.LeadCreateDTO;
import com.b2b.b2b.modules.crm.lead.payloads.LeadResponseDTO;

public interface LeadService {
    LeadResponseDTO createLead(LeadCreateDTO leadCreateDTO, User user);
    void updateLead(Integer leadId,  LeadCreateDTO leadCreateDTO);
}
