package com.b2b.b2b.modules.crm.deal.controller;

import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.deal.payloads.DealCreateRequestDTO;
import com.b2b.b2b.modules.crm.deal.payloads.DealResponseDTO;
import com.b2b.b2b.modules.crm.deal.service.DealService;
import com.b2b.b2b.modules.crm.lead.payloads.LeadResponseDTO;
import com.b2b.b2b.shared.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("app/v1/deals")
public class DealController {

    private final AuthUtil authUtil;
    private final DealService dealService;

    public DealController(AuthUtil authUtil, DealService dealService) {
        this.authUtil = authUtil;
        this.dealService = dealService;
    }

    @PostMapping("")
    public ResponseEntity<DealResponseDTO> createDeal(@Valid  @RequestBody DealCreateRequestDTO dealCreateRequestDTO){
        User user = authUtil.loggedInUser();
        DealResponseDTO dealResponseDTO = dealService.createDeal(dealCreateRequestDTO, user);
        return new ResponseEntity<DealResponseDTO>(dealResponseDTO, HttpStatus.CREATED);
    }
    @GetMapping("") //including user owned deals
    public ResponseEntity<?> getAllDeals(){
        User user = authUtil.loggedInUser();
        List<DealResponseDTO> dealResponseDTOs  = dealService.getAllDeals(user);
        return new ResponseEntity<>(dealResponseDTOs, HttpStatus.OK);
    }
    @GetMapping("/userOwned")
    public ResponseEntity<?> getAllUserOwnedDeals() {
        User user = authUtil.loggedInUser();
        List<DealResponseDTO> dealResponseDTOs = dealService.getAllUserOwnedDeals(user);
        return new  ResponseEntity<>(dealResponseDTOs,HttpStatus.OK);
    }
    @GetMapping("/{dealId}")
    public ResponseEntity<?> getDealById(@PathVariable("dealId") Integer dealId){
        User user = authUtil.loggedInUser();
        DealResponseDTO dealResponseDTO = dealService.getDealById(dealId, user);
        return new ResponseEntity<>(dealResponseDTO, HttpStatus.OK);
    }
}
