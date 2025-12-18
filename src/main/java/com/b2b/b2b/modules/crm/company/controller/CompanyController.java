package com.b2b.b2b.modules.crm.company.controller;

import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.company.payloads.CompanyDTO;
import com.b2b.b2b.modules.crm.company.payloads.CompanyResponseDTO;
import com.b2b.b2b.modules.crm.company.service.CompanyService;
import com.b2b.b2b.modules.crm.contact.payloads.ContactResponseDTO;
import com.b2b.b2b.shared.AuthUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("app/v1/companies")
public class CompanyController {

    private final AuthUtil authUtil;
    private final CompanyService companyService;

    public CompanyController(AuthUtil authUtil, CompanyService companyService) {
        this.authUtil = authUtil;
        this.companyService = companyService;
    }

    @PostMapping("")
    public ResponseEntity<?> addCompany(@RequestBody CompanyDTO companyDTO) {
        User user = authUtil.loggedInUser();
        CompanyResponseDTO companyResponseDTO  = companyService.addCompany(companyDTO, user);
        return new ResponseEntity<>(companyResponseDTO,HttpStatus.CREATED);
    }

    @GetMapping("")
    public ResponseEntity<?> getAllCompanies() {
        User user  = authUtil.loggedInUser();
        List<CompanyResponseDTO> companyResponseDTOs = companyService.getAllCompanies(user);
        return new ResponseEntity<>(companyResponseDTOs, HttpStatus.OK);
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<?> getCompany(@PathVariable("companyId") Integer companyId) {
        User user = authUtil.loggedInUser();
        CompanyResponseDTO companyResponseDTO = companyService.getCompany(companyId, user);
        return new ResponseEntity<>(companyResponseDTO, HttpStatus.OK);
    }

    @GetMapping("/{companyId}/contacts")
    public ResponseEntity<?> getCompanyContacts(@PathVariable("companyId") Integer companyId) {
        User user = authUtil.loggedInUser();
        List<ContactResponseDTO> contactResponseDTOs = companyService.getCompanyContacts(companyId, user);
        return new ResponseEntity<>(contactResponseDTOs, HttpStatus.OK);
    }
}
