package com.b2b.b2b.modules.crm.lead.util;

import com.b2b.b2b.modules.auth.payloads.OrganizationDTO;
import com.b2b.b2b.modules.crm.company.payloads.CompanyResponseDTO;
import com.b2b.b2b.modules.crm.lead.entity.Lead;
import com.b2b.b2b.modules.crm.lead.payloads.LeadResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class LeadUtils {
    public LeadResponseDTO createLeadResponseDTO(Lead lead)
    {
        OrganizationDTO org = new  OrganizationDTO(
                lead.getOrganization().getOrganizationName(),
                lead.getOrganization().getCreatedAt()

        );
        CompanyResponseDTO company = new CompanyResponseDTO(
                lead.getCompany().getId(),
                lead.getCompany().getCompanyName(),
                lead.getCompany().getWebsite(),
                lead.getCompany().getIndustry()
        );

        String currentStageName = lead.getPipelineStage().getStageName();
        Integer currentStageOrder = lead.getPipelineStage().getStageOrder();

        int totalStages = lead.getPipeline().getPipelineStages().size();
        double progressPercentage = (totalStages > 0) ?  ((double)currentStageOrder / totalStages) * 100 : 0;


        return  new LeadResponseDTO(
                lead.getId(),
                lead.getLeadName(),
                lead.getLeadEmail(),
                lead.getLeadPhone(),
                lead.getLeadStatus(),
                currentStageName,
                currentStageOrder,
                progressPercentage,
                lead.getCreatedAt(),
                org,
                company);
    }

}
