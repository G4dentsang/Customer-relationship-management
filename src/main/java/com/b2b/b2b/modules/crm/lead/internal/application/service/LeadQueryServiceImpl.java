package com.b2b.b2b.modules.crm.lead.internal.application.service;

import com.b2b.b2b.exception.ResourceNotFoundException;
import com.b2b.b2b.modules.crm.lead.internal.infrastructure.web.dto.LeadFilterDTO;
import com.b2b.b2b.modules.crm.lead.internal.infrastructure.web.dto.LeadResponseDTO;
import com.b2b.b2b.modules.crm.lead.internal.infrastructure.persistence.Lead;
import com.b2b.b2b.modules.crm.lead.internal.application.port.in.LeadQueryUseCase;
import com.b2b.b2b.modules.crm.lead.internal.infrastructure.persistence.LeadRepository;
import com.b2b.b2b.modules.crm.lead.internal.util.LeadSpecifications;
import com.b2b.b2b.modules.crm.lead.internal.util.LeadUtils;
import com.b2b.b2b.shared.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LeadQueryServiceImpl implements LeadQueryUseCase {
    private final Helpers helpers;
    private final LeadRepository leadRepository;
    private final AuthUtil authUtil;
    private final LeadUtils leadUtils;

    @Override
    public Page<LeadResponseDTO> findAllByOrganization(LeadFilterDTO filter, Pageable pageable) {
        Specification<Lead> spec = LeadSpecifications.createSearch(filter);
        return helpers.toDTOList(leadRepository.findAll(spec,pageable));
    }

    @Override
    public Page<LeadResponseDTO> findMyList(LeadFilterDTO filter, Pageable pageable) {
        filter.setOwnerId(authUtil.loggedInUserId());
        Specification<Lead> spec = LeadSpecifications.createSearch(filter);
        return helpers.toDTOList(leadRepository.findAll(spec, pageable));
    }

    @Override
    public LeadResponseDTO getById(Integer leadId) {
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead", "id", leadId));
        return leadUtils.createLeadResponseDTO(lead);
    }

}
