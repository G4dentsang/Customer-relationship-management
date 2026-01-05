package com.b2b.b2b.modules.crm.contact.controller;

import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.contact.payloads.ContactDTO;
import com.b2b.b2b.modules.crm.contact.payloads.ContactResponseDTO;
import com.b2b.b2b.modules.crm.contact.service.ContactService;
import com.b2b.b2b.modules.crm.deal.payloads.DealResponseDTO;
import com.b2b.b2b.modules.crm.deal.service.DealService;
import com.b2b.b2b.shared.AuthUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("app/v1/contacts")
@RequiredArgsConstructor
public class ContactController {
    private final ContactService contactService;
    private final AuthUtil authUtil;
    private final DealService dealService;

    @PostMapping
    public ResponseEntity<ContactResponseDTO> create(@Valid @RequestBody ContactDTO request) {
        User user = authUtil.loggedInUser();
        return ResponseEntity.status(HttpStatus.CREATED).body(contactService.add(request, user));
    }

    @GetMapping
    public ResponseEntity<List<ContactResponseDTO>> listAll() {
        User user = authUtil.loggedInUser();
        return ResponseEntity.ok(contactService.getContacts(user));
    }

    @GetMapping("/{contactId}")
    public ResponseEntity<ContactResponseDTO> getById(@PathVariable Integer contactId) {
        User user = authUtil.loggedInUser();
        return ResponseEntity.ok(contactService.get(contactId, user));
    }

    @PatchMapping("/{contactId}")
    public ResponseEntity<ContactResponseDTO> update(@Valid @RequestBody ContactDTO request, @PathVariable Integer contactId) {
        User user = authUtil.loggedInUser();
        return ResponseEntity.ok(contactService.update(contactId, request, user));
    }

    @DeleteMapping("/{contactId}")
    public ResponseEntity<Void> delete(@PathVariable Integer contactId) {
        User user = authUtil.loggedInUser();
        contactService.delete(contactId, user);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("{/contactId}/deals")
    public ResponseEntity<List<DealResponseDTO>> getDeals(@PathVariable Integer contactId) {
        User user = authUtil.loggedInUser();
        return ResponseEntity.ok(dealService.getContactDeals(contactId, user));
    }

}
