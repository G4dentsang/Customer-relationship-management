package com.b2b.b2b.modules.crm.contact.controller;

import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.contact.payloads.ContactDTO;
import com.b2b.b2b.modules.crm.contact.payloads.ContactResponseDTO;
import com.b2b.b2b.modules.crm.contact.service.ContactService;
import com.b2b.b2b.shared.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("app/v1/contacts")
public class ContactController {
    private final ContactService contactService;
    private final AuthUtil authUtil;

    public ContactController(ContactService contactService, AuthUtil authUtil) {
        this.contactService = contactService;
        this.authUtil = authUtil;
    }

    @PostMapping("")
    public ResponseEntity<?> addContact(@Valid @RequestBody ContactDTO contactDTO) {
        User user = authUtil.loggedInUser();
        ContactResponseDTO contactResponseDTO = contactService.addContact(contactDTO, user);
        return new ResponseEntity<>(contactResponseDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{contactId}")
    public ResponseEntity<?> getContact(@PathVariable("contactId") Integer contactId) {
        User user = authUtil.loggedInUser();
        ContactResponseDTO contactResponseDTO = contactService.getContact(contactId, user);
        return new ResponseEntity<>(contactResponseDTO, HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<?> getAllContacts() {
        User user = authUtil.loggedInUser();
        List<ContactResponseDTO> contactResponseDTOs = contactService.getAllContacts(user);
        return new ResponseEntity<>(contactResponseDTOs, HttpStatus.OK);
    }

}
