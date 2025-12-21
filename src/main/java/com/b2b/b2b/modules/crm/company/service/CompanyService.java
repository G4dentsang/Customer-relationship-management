package com.b2b.b2b.modules.crm.company.service;

import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.company.payloads.CompanyDTO;
import com.b2b.b2b.modules.crm.company.payloads.CompanyResponseDTO;
import com.b2b.b2b.modules.crm.contact.payloads.ContactResponseDTO;
import com.b2b.b2b.modules.crm.deal.payloads.DealResponseDTO;

import java.util.List;

public interface CompanyService {
    CompanyResponseDTO addCompany(CompanyDTO companyDTO, User user);
    List<CompanyResponseDTO> getAllCompanies(User user);
    CompanyResponseDTO getCompany(Integer companyId, User user);
    List<ContactResponseDTO> getCompanyContacts(Integer companyId, User user);
    List<DealResponseDTO> getCompanyDeals(Integer companyId, User user);
}
