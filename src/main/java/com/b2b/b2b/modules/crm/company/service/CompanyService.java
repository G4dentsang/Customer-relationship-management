package com.b2b.b2b.modules.crm.company.service;

import com.b2b.b2b.modules.crm.company.payloads.CompanyDTO;
import com.b2b.b2b.modules.crm.company.payloads.CompanyResponseDTO;

import java.util.List;

public interface CompanyService {
    CompanyResponseDTO create(CompanyDTO request);
    List<CompanyResponseDTO> listAll();
    CompanyResponseDTO getById(Integer id);
    CompanyResponseDTO update(Integer id, CompanyDTO request);
    CompanyResponseDTO delete(Integer id);

}
