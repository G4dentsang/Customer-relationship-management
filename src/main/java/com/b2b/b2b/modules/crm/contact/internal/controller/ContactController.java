package com.b2b.b2b.modules.crm.contact.internal.controller;

import com.b2b.b2b.config.AppConstants;
import com.b2b.b2b.modules.crm.contact.internal.dto.ContactRequestDTO;
import com.b2b.b2b.modules.crm.contact.internal.dto.ContactFilterDTO;
import com.b2b.b2b.modules.crm.contact.internal.dto.ContactResponseDTO;
import com.b2b.b2b.modules.crm.contact.internal.service.ContactService;
import com.b2b.b2b.modules.crm.deal.api.DealPublicApi;
import com.b2b.b2b.modules.crm.deal.internal.infrastructure.web.dto.DealResponseDTO;
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
@RequestMapping("app/v1/contacts")
@RequiredArgsConstructor
public class ContactController {
    private final ContactService contactService;
    private final DealPublicApi api;

    @PostMapping
    public ResponseEntity<ContactResponseDTO> create(@Valid @RequestBody ContactRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contactService.create(request));
    }

    @GetMapping
    public ResponseEntity<Page<ContactResponseDTO>> listAll(ContactFilterDTO filter, @PageableDefault(size = AppConstants.DEFAULT_SIZE, sort = "lastName", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(contactService.getContacts(filter, pageable));
    }

    @GetMapping("/{contactId}")
    public ResponseEntity<ContactResponseDTO> getById(@PathVariable Integer contactId) {
        return ResponseEntity.ok(contactService.get(contactId));
    }

    @PatchMapping("/{contactId}")
    public ResponseEntity<ContactResponseDTO> update(@Valid @RequestBody ContactRequestDTO request, @PathVariable Integer contactId) {
        return ResponseEntity.ok(contactService.update(contactId, request));
    }

    @DeleteMapping("/{contactId}")
    public ResponseEntity<Void> delete(@PathVariable Integer contactId) {
        contactService.delete(contactId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{contactId}/deals")
    public ResponseEntity<Page<DealResponseDTO>> getDeals(@PathVariable Integer contactId, @PageableDefault(size = AppConstants.DEFAULT_SIZE, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(api.getContactDeals(contactId, pageable));
    }

}
