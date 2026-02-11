package com.b2b.b2b.modules.crm.deal.internal.application.service;

import com.b2b.b2b.exception.ResourceNotFoundException;
import com.b2b.b2b.modules.crm.deal.internal.infrastructure.web.dto.DealFilterDTO;
import com.b2b.b2b.modules.crm.deal.internal.infrastructure.web.dto.DealResponseDTO;
import com.b2b.b2b.modules.crm.deal.internal.application.port.in.DealQueryUseCase;
import com.b2b.b2b.modules.crm.deal.internal.infrastructure.persistence.DealRepository;
import com.b2b.b2b.modules.crm.deal.internal.infrastructure.persistence.Deal;
import com.b2b.b2b.modules.crm.deal.internal.utils.DealSpecifications;
import com.b2b.b2b.modules.crm.deal.internal.utils.DealUtils;
import com.b2b.b2b.shared.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DealQueryServiceImpl implements DealQueryUseCase {
    private final Helpers helpers;
    private final DealRepository dealRepository;
    private final AuthUtil authUtil;
    private final DealUtils dealUtils;

    @Override
    public Page<DealResponseDTO> findAll(DealFilterDTO filter, Pageable pageable) {
        Specification<Deal> spec = DealSpecifications.createSearch(filter);
        return helpers.toDTOList(dealRepository.findAll(spec, pageable));
    }

    @Override
    public Page<DealResponseDTO> findAllByOwner(DealFilterDTO filter, Pageable pageable) {
        if(filter.getOwnerId() == null) filter.setOwnerId(authUtil.loggedInUserId());
        Specification<Deal> spec = DealSpecifications.createSearch(filter);
        return helpers.toDTOList(dealRepository.findAll(spec, pageable));
    }

    @Override
    public DealResponseDTO getById(Integer id) {
        Deal deal = dealRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Deal", "id", id));
        return dealUtils.createDealResponseDTO(deal);
    }

}
