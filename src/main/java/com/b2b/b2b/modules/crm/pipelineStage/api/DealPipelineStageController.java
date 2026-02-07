package com.b2b.b2b.modules.crm.pipelineStage.api;

import com.b2b.b2b.modules.crm.pipelineStage.payloads.PipelineStageRequestDTO;
import com.b2b.b2b.modules.crm.pipelineStage.payloads.PipelineStageResponseDTO;
import com.b2b.b2b.modules.crm.pipelineStage.service.DealPipelineStageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/app/v1/pipelines/{pipelineId}/stages")
@RequiredArgsConstructor
public class DealPipelineStageController {
    private final DealPipelineStageService dealPipelineStageService;

    @PostMapping()
    public ResponseEntity<List<PipelineStageResponseDTO>> add(@PathVariable Integer pipelineId, @Valid @RequestBody List<PipelineStageRequestDTO> request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(dealPipelineStageService.addStage(pipelineId, request));
    }

    @GetMapping()
    public ResponseEntity<List<PipelineStageResponseDTO>> listAll(@PathVariable Integer pipelineId) {
        return ResponseEntity.ok(dealPipelineStageService.getAllStages(pipelineId));
    }

    @PatchMapping("/{stageId}")
    public ResponseEntity<List<PipelineStageResponseDTO>> update(@PathVariable Integer pipelineId, @PathVariable Integer stageId , @Valid @RequestBody PipelineStageRequestDTO request) {
        return ResponseEntity.ok(dealPipelineStageService.updateStage(pipelineId, stageId, request));
    }

    @DeleteMapping("/{stageId}")
    public ResponseEntity<List<PipelineStageResponseDTO>> deleteStage(@PathVariable Integer pipelineId, @PathVariable Integer stageId, @RequestParam Integer targetStageId) {
        return ResponseEntity.ok(dealPipelineStageService.deleteStage(pipelineId, stageId, targetStageId));
    }

}
