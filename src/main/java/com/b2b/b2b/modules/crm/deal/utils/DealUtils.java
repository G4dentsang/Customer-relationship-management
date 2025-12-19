package com.b2b.b2b.modules.crm.deal.utils;

import com.b2b.b2b.modules.crm.company.payloads.CompanyResponseDTO;
import com.b2b.b2b.modules.crm.deal.entity.Deals;
import com.b2b.b2b.modules.crm.deal.payloads.DealResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class DealUtils {
    public DealResponseDTO createDealResponseDTO(Deals deal) {

            CompanyResponseDTO company = new  CompanyResponseDTO(
                    deal.getCompany().getId(),
                    deal.getCompany().getCompanyName(),
                    deal.getCompany().getWebsite(),
                    deal.getCompany().getIndustry()
            );

            return  new DealResponseDTO(
                    deal.getId(),
                    deal.getDealName(),
                    deal.getDealAmount(),
                    deal.getDealStatus(),
                    deal.getCreatedAt(),
                    deal.getClosedAt(),
                    company
            );
        }
}
