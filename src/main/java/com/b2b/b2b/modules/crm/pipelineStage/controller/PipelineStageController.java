package com.b2b.b2b.modules.crm.pipelineStage.controller;

import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.pipelineStage.payloads.PipelineStageRequestDTO;
import com.b2b.b2b.modules.crm.pipelineStage.payloads.PipelineStageResponseDTO;
import com.b2b.b2b.modules.crm.pipelineStage.service.PipelineStageService;
import com.b2b.b2b.shared.AuthUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/app/v1/pipelines")
@RequiredArgsConstructor
public class PipelineStageController {
    private final AuthUtil authUtil;
    private final PipelineStageService pipelineStageService;

    @PostMapping("/{pipelineId}/stages")
    public ResponseEntity<List<PipelineStageResponseDTO>> create(@PathVariable Integer pipelineId, @Valid @RequestBody List<PipelineStageRequestDTO> request) {
        User user = authUtil.loggedInUser();
        return ResponseEntity.status(HttpStatus.CREATED).body(pipelineStageService.addPipelineStage(pipelineId, request, user));
    }

    @GetMapping("{pipelineId}/stages")
    public ResponseEntity<List<PipelineStageResponseDTO>> get(@PathVariable Integer pipelineId) {
        User user = authUtil.loggedInUser();
        return ResponseEntity.ok(pipelineStageService.getPipelineStage(pipelineId, user));
    }

}
