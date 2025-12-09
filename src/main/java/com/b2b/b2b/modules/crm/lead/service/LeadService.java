package com.b2b.b2b.modules.crm.lead.service;

import com.b2b.b2b.modules.crm.lead.payloads.LeadCreateDTO;

public interface LeadService {
    void createLead(LeadCreateDTO leadCreateDTO);
    void updateLead(Integer leadId,  LeadCreateDTO leadCreateDTO);
}
