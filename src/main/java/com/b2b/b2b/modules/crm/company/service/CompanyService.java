package com.b2b.b2b.modules.crm.company.service;

import com.b2b.b2b.modules.crm.company.payloads.CompanyDTO;
import com.b2b.b2b.modules.crm.company.payloads.CompanyFilterDTO;
import com.b2b.b2b.modules.crm.company.payloads.CompanyResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface CompanyService {
    CompanyResponseDTO create(CompanyDTO request);
    Page<CompanyResponseDTO> listAll(CompanyFilterDTO filter, Pageable pageable);
    CompanyResponseDTO getById(Integer id);
    CompanyResponseDTO update(Integer id, CompanyDTO request);
    CompanyResponseDTO delete(Integer id);

}
