package com.b2b.b2b.modules.crm.pipelineStage.controller;

import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.pipelineStage.payloads.PipelineStageRequestDTO;
import com.b2b.b2b.modules.crm.pipelineStage.payloads.PipelineStageResponseDTO;
import com.b2b.b2b.modules.crm.pipelineStage.service.PipelineStageService;
import com.b2b.b2b.shared.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/app/v1/pipelines")
public class PipelineStageController {
    private final AuthUtil authUtil;
    private final PipelineStageService pipelineStageService;

    public PipelineStageController(AuthUtil authUtil, PipelineStageService pipelineStageService) {
        this.authUtil = authUtil;
        this.pipelineStageService = pipelineStageService;
    }

    @PostMapping("/{pipelineId}/stages")
    public ResponseEntity<?> createPipelineStage(@PathVariable("pipelineId") Integer pipelineId, @Valid  @RequestBody List<PipelineStageRequestDTO> pipelineStageRequestDTO){
        User user = authUtil.loggedInUser();
        List<PipelineStageResponseDTO> pipelineStageResponseDTOs = pipelineStageService.addPipelineStage(pipelineId,pipelineStageRequestDTO,user);
        return new ResponseEntity<>(pipelineStageResponseDTOs, HttpStatus.CREATED);
    }
    @GetMapping("{pipelineId}/stages")
    public ResponseEntity<?> getPipelineStage(@PathVariable("pipelineId") Integer pipelineId){
        User user = authUtil.loggedInUser();
        List<PipelineStageResponseDTO> pipelineStageResponseDTOs = pipelineStageService.getPipelineStage(pipelineId,user);
        return new ResponseEntity<>(pipelineStageResponseDTOs, HttpStatus.OK);
    }

}
