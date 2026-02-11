package com.b2b.b2b.modules.crm.deal.internal.infrastructure.web;

import com.b2b.b2b.config.AppConstants;
import com.b2b.b2b.modules.crm.deal.internal.application.port.in.DealCommandUseCase;
import com.b2b.b2b.modules.crm.deal.internal.application.port.in.DealQueryUseCase;
import com.b2b.b2b.modules.crm.deal.internal.infrastructure.web.dto.*;
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

    private final DealQueryUseCase query;
    private final DealCommandUseCase command;

    @PostMapping
    public ResponseEntity<DealResponseDTO> createDeal(@Valid @RequestBody DealCreateRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(command.create(request));
    }

    @GetMapping
    public ResponseEntity<Page<DealResponseDTO>> listAll(DealFilterDTO filter, @PageableDefault(size = AppConstants.DEFAULT_SIZE, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(query.findAll(filter, pageable));
    }

    @GetMapping("/my-deals")
    public ResponseEntity<Page<DealResponseDTO>> listMine(DealFilterDTO filter, @PageableDefault(size = AppConstants.DEFAULT_SIZE, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(query.findAllByOwner(filter, pageable));
    }

    @GetMapping("/{dealId}")
    public ResponseEntity<DealResponseDTO> get(@PathVariable Integer dealId) {
        return ResponseEntity.ok(query.getById(dealId));
    }

    @PatchMapping("/{dealId}")
    public ResponseEntity<DealResponseDTO> update(@PathVariable Integer dealId, @Valid @RequestBody DealUpdateDTO request) {
        return ResponseEntity.ok(command.update(dealId, request));
    }

    @PatchMapping("/{dealId}/status")
    public ResponseEntity<DealResponseDTO> status(@PathVariable Integer dealId, @Valid @RequestBody DealUpdateStatusRequestDTO request) {
        DealUpdateDTO mainDTO =  new DealUpdateDTO();
        mainDTO.setDealStatus(request.getStatus());
        return ResponseEntity.ok(command.update(dealId, mainDTO));
    }

    @DeleteMapping("/{dealId}")
    public ResponseEntity<DealResponseDTO> delete(@PathVariable Integer dealId) {
        command.delete(dealId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    /* undo delete button */
}
