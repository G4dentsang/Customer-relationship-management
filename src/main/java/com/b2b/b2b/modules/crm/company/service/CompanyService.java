package com.b2b.b2b.modules.crm.company.service;

import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.company.payloads.CompanyDTO;
import com.b2b.b2b.modules.crm.company.payloads.CompanyResponseDTO;

import java.util.List;

public interface CompanyService {
    CompanyResponseDTO create(CompanyDTO request, User user);
    List<CompanyResponseDTO> listAll(User user);
    CompanyResponseDTO getById(Integer id, User user);

}
