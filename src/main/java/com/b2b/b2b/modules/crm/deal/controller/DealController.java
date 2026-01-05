package com.b2b.b2b.modules.crm.deal.controller;

import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.deal.payloads.DealCreateRequestDTO;
import com.b2b.b2b.modules.crm.deal.payloads.DealResponseDTO;
import com.b2b.b2b.modules.crm.deal.payloads.DealUpdateDTO;
import com.b2b.b2b.modules.crm.deal.payloads.DealUpdateStatusRequestDTO;
import com.b2b.b2b.modules.crm.deal.service.DealService;
import com.b2b.b2b.shared.AuthUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("app/v1/deals")
@RequiredArgsConstructor
public class DealController {

    private final AuthUtil authUtil;
    private final DealService dealService;

    @PostMapping
    public ResponseEntity<DealResponseDTO> createDeal(@Valid @RequestBody DealCreateRequestDTO request) {
        User user = authUtil.loggedInUser();
        return ResponseEntity.status(HttpStatus.CREATED).body(dealService.create(request, user));
    }

    @GetMapping
    public ResponseEntity<List<DealResponseDTO>> listAll() {
        User user = authUtil.loggedInUser();
        return ResponseEntity.ok(dealService.findAllByOrganization(user));
    }

    @GetMapping("/my-deals")
    public ResponseEntity<List<DealResponseDTO>> listMine() {
        User user = authUtil.loggedInUser();
        return ResponseEntity.ok(dealService.findAllByUser(user));
    }

    @GetMapping("/{dealId}")
    public ResponseEntity<DealResponseDTO> get(@PathVariable Integer dealId) {
        User user = authUtil.loggedInUser();
        return ResponseEntity.ok(dealService.getById(dealId, user));
    }

    @PatchMapping("/{dealId}")
    public ResponseEntity<DealResponseDTO> update(@PathVariable Integer dealId, @Valid @RequestBody DealUpdateDTO request) {
        User user = authUtil.loggedInUser();
        return ResponseEntity.ok(dealService.update(dealId, request, user));
    }

    @PatchMapping("/{dealId}/status")
    public ResponseEntity<DealResponseDTO> status(@PathVariable Integer dealId, @Valid @RequestBody DealUpdateStatusRequestDTO request) {
        User user = authUtil.loggedInUser();
        DealUpdateDTO mainDTO =  new DealUpdateDTO();
        mainDTO.setDealStatus(request.getStatus());
        return ResponseEntity.ok(dealService.update(dealId, mainDTO, user));
    }

    @DeleteMapping("/{dealId}")
    public ResponseEntity<DealResponseDTO> delete(@PathVariable Integer dealId) {
        User user = authUtil.loggedInUser();
        dealService.delete(dealId, user);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    //undo delete button ****
}
