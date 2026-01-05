package com.b2b.b2b.modules.crm.company.controller;

import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.company.payloads.CompanyDTO;
import com.b2b.b2b.modules.crm.company.payloads.CompanyResponseDTO;
import com.b2b.b2b.modules.crm.company.service.CompanyService;
import com.b2b.b2b.modules.crm.contact.payloads.ContactResponseDTO;
import com.b2b.b2b.modules.crm.contact.service.ContactService;
import com.b2b.b2b.modules.crm.deal.payloads.DealResponseDTO;
import com.b2b.b2b.modules.crm.deal.service.DealService;
import com.b2b.b2b.shared.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("app/v1/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final AuthUtil authUtil;
    private final CompanyService companyService;
    private final DealService dealService;
    private final ContactService contactService;

    @PostMapping
    public ResponseEntity<CompanyResponseDTO> add(@RequestBody CompanyDTO request) {
        User user = authUtil.loggedInUser();
        return ResponseEntity.status(HttpStatus.CREATED).body(companyService.create(request, user));
    }

    @GetMapping
    public ResponseEntity<List<CompanyResponseDTO>> listAll() {
        User user = authUtil.loggedInUser();
        return ResponseEntity.ok(companyService.listAll(user));
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<CompanyResponseDTO> get(@PathVariable Integer companyId) {
        User user = authUtil.loggedInUser();
        return ResponseEntity.ok(companyService.getById(companyId, user));
    }

    @PatchMapping("/{companyId}")
    public ResponseEntity<CompanyResponseDTO> update(@PathVariable Integer companyId, @RequestBody CompanyDTO request) {
        User user = authUtil.loggedInUser();
        return ResponseEntity.ok(companyService.update(companyId, request, user));
    }

    @GetMapping("/{companyId}/contacts")
    public ResponseEntity<List<ContactResponseDTO>> getContacts(@PathVariable Integer companyId) {
        User user = authUtil.loggedInUser();
        return ResponseEntity.ok(contactService.getCompanyContacts(companyId, user));
    }

    @GetMapping("/{companyId}/deals")
    public ResponseEntity<List<DealResponseDTO>> getDeals(@PathVariable Integer companyId) {
        User user = authUtil.loggedInUser();
        return ResponseEntity.ok(dealService.getCompanyDeals(companyId, user));
    }

    @DeleteMapping("/{companyId}")
    public ResponseEntity<CompanyResponseDTO> delete(@PathVariable Integer companyId) {
        User user = authUtil.loggedInUser();
        return ResponseEntity.ok(companyService.delete(companyId, user));
    }
}
