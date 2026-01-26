package com.b2b.b2b.modules.crm.company.controller;

import com.b2b.b2b.config.AppConstants;
import com.b2b.b2b.modules.crm.company.payloads.CompanyDTO;
import com.b2b.b2b.modules.crm.company.payloads.CompanyFilterDTO;
import com.b2b.b2b.modules.crm.company.payloads.CompanyResponseDTO;
import com.b2b.b2b.modules.crm.company.service.CompanyService;
import com.b2b.b2b.modules.crm.contact.payloads.ContactResponseDTO;
import com.b2b.b2b.modules.crm.contact.service.ContactService;
import com.b2b.b2b.modules.crm.deal.payloads.DealResponseDTO;
import com.b2b.b2b.modules.crm.deal.service.DealService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("app/v1/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;
    private final DealService dealService;
    private final ContactService contactService;

    @PostMapping
    public ResponseEntity<CompanyResponseDTO> add(@RequestBody CompanyDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(companyService.create(request));
    }

    @GetMapping
    public ResponseEntity<Page<CompanyResponseDTO>> listAll(CompanyFilterDTO filter, @PageableDefault(size = AppConstants.DEFAULT_SIZE, sort = "companyName", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(companyService.listAll(filter, pageable));
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<CompanyResponseDTO> get(@PathVariable Integer companyId) {
        return ResponseEntity.ok(companyService.getById(companyId));
    }

    @PatchMapping("/{companyId}")
    public ResponseEntity<CompanyResponseDTO> update(@PathVariable Integer companyId, @RequestBody CompanyDTO request) {
        return ResponseEntity.ok(companyService.update(companyId, request));
    }

    @GetMapping("/{companyId}/contacts")
    public ResponseEntity<Page<ContactResponseDTO>> getContacts(@PathVariable Integer companyId,@PageableDefault(size = AppConstants.DEFAULT_SIZE, sort = "lastName", direction = Sort.Direction.DESC) Pageable pageable ) {
        return ResponseEntity.ok(contactService.getCompanyContacts(companyId, pageable));
    }

    @GetMapping("/{companyId}/deals")
    public ResponseEntity<Page<DealResponseDTO>> getDeals(@PathVariable Integer companyId, @PageableDefault(size = AppConstants.DEFAULT_SIZE, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(dealService.getCompanyDeals(companyId, pageable));
    }

    @DeleteMapping("/{companyId}")
    public ResponseEntity<CompanyResponseDTO> delete(@PathVariable Integer companyId) {
        return ResponseEntity.ok(companyService.delete(companyId));
    }
}
