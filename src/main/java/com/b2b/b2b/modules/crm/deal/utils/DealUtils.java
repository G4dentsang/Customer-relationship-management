package com.b2b.b2b.modules.crm.deal.utils;

import com.b2b.b2b.modules.crm.company.payloads.CompanyResponseDTO;
import com.b2b.b2b.modules.crm.deal.entity.Deal;
import com.b2b.b2b.modules.crm.deal.payloads.DealResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class DealUtils {
    public DealResponseDTO createDealResponseDTO(Deal deal) {

            CompanyResponseDTO company = new  CompanyResponseDTO(
                    deal.getCompany().getId(),
                    deal.getCompany().getCompanyName(),
                    deal.getCompany().getWebsite(),
                    deal.getCompany().getIndustry()
            );
            String currentStageName = deal.getPipelineStage().getStageName();
            Integer currentStageOrder = deal.getPipelineStage().getStageOrder();

            int totalStages = deal.getPipeline().getPipelineStages().size();
            double progressPercentage = (totalStages > 0) ?  ((double)currentStageOrder / totalStages) * 100 : 0;

            return  new DealResponseDTO(
                    deal.getId(),
                    deal.getDealName(),
                    deal.getDealAmount(),
                    deal.getDealStatus(),
                    currentStageName,
                    currentStageOrder,
                    progressPercentage,
                    deal.getCreatedAt(),
                    deal.getClosedAt(),
                    company
            );
        }
}
