package com.b2b.b2b.modules.crm.deal.controller;

import com.b2b.b2b.config.AppConstants;
import com.b2b.b2b.modules.crm.deal.payloads.*;
import com.b2b.b2b.modules.crm.deal.service.DealService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("app/v1/deals")
@RequiredArgsConstructor
public class DealController {
    private final DealService dealService;

    @PostMapping
    public ResponseEntity<DealResponseDTO> createDeal(@Valid @RequestBody DealCreateRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(dealService.create(request));
    }

    @GetMapping
    public ResponseEntity<Page<DealResponseDTO>> listAll(DealFilterDTO filter, @PageableDefault(size = AppConstants.DEFAULT_SIZE, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(dealService.findAll(filter, pageable));
    }

    @GetMapping("/my-deals")
    public ResponseEntity<Page<DealResponseDTO>> listMine(DealFilterDTO filter, @PageableDefault(size = AppConstants.DEFAULT_SIZE, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(dealService.findAllByOwner(filter, pageable));
    }

    @GetMapping("/{dealId}")
    public ResponseEntity<DealResponseDTO> get(@PathVariable Integer dealId) {
        return ResponseEntity.ok(dealService.getById(dealId));
    }

    @PatchMapping("/{dealId}")
    public ResponseEntity<DealResponseDTO> update(@PathVariable Integer dealId, @Valid @RequestBody DealUpdateDTO request) {
        return ResponseEntity.ok(dealService.update(dealId, request));
    }

    @PatchMapping("/{dealId}/status")
    public ResponseEntity<DealResponseDTO> status(@PathVariable Integer dealId, @Valid @RequestBody DealUpdateStatusRequestDTO request) {
        DealUpdateDTO mainDTO =  new DealUpdateDTO();
        mainDTO.setDealStatus(request.getStatus());
        return ResponseEntity.ok(dealService.update(dealId, mainDTO));
    }

    @DeleteMapping("/{dealId}")
    public ResponseEntity<DealResponseDTO> delete(@PathVariable Integer dealId) {
        dealService.delete(dealId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    /* undo delete button */
}
