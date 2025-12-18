package com.b2b.b2b.modules.crm.lead.util;

import com.b2b.b2b.modules.auth.payloads.OrganizationDTO;
import com.b2b.b2b.modules.crm.company.payloads.CompanyDTO;
import com.b2b.b2b.modules.crm.lead.entity.Lead;
import com.b2b.b2b.modules.crm.lead.payloads.LeadResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class LeadUtils {
    public LeadResponseDTO createLeadResponseDTO(Lead lead)
    {
        OrganizationDTO orgDTO = new  OrganizationDTO(
                lead.getOrganization().getOrganizationName(),
                lead.getOrganization().getCreatedAt()

        );
        CompanyDTO companyDTO = new  CompanyDTO(
                lead.getCompany().getCompanyName(),
                lead.getCompany().getWebsite(),
                lead.getCompany().getIndustry()
        );

        return  new LeadResponseDTO(
                lead.getId(),
                lead.getLeadName(),
                lead.getLeadEmail(),
                lead.getLeadPhone(),
                lead.getLeadStatus(),
                lead.getCreatedAt(),
                orgDTO,
                companyDTO);
    }

}
