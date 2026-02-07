package com.b2b.b2b.modules.crm.lead.util;

import com.b2b.b2b.modules.auth.payloads.MemberResponseDTO;
import com.b2b.b2b.modules.auth.payloads.OrganizationDTO;
import com.b2b.b2b.modules.auth.util.UserUtils;
import com.b2b.b2b.modules.crm.company.payloads.CompanyResponseDTO;
import com.b2b.b2b.modules.crm.lead.entity.Lead;
import com.b2b.b2b.modules.crm.lead.payloads.LeadResponseDTO;
import com.b2b.b2b.shared.multitenancy.OrganizationContext;
import org.springframework.stereotype.Component;

@Component
public class LeadUtils {
    private final UserUtils userUtils;

    public LeadUtils(UserUtils userUtils) {
        this.userUtils = userUtils;
    }

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

       MemberResponseDTO assignedUser =  lead.getAssignedUser()
               .getUserOrganizations()
               .stream()
               .filter(uo -> uo.getOrganization().getOrganizationId().equals(OrganizationContext.getOrgId()))
               .findFirst()
               .map(uo ->  userUtils.createMemberResponseDTO(lead.getAssignedUser(), uo.getRole()))
               .orElse(null);


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
                lead.getUpdatedAt(),
                assignedUser,
                org,
                company);
    }

}
