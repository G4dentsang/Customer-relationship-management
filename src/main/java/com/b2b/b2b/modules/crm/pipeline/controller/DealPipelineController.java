package com.b2b.b2b.modules.crm.pipeline.controller;

import com.b2b.b2b.config.AppConstants;
import com.b2b.b2b.modules.crm.pipeline.payloads.*;
import com.b2b.b2b.modules.crm.pipeline.service.DealPipelineService;
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
@RequestMapping("app/v1/deals/pipelines")
@RequiredArgsConstructor
public class DealPipelineController {

    private final DealPipelineService dealPipelineService;

    @PostMapping
    public ResponseEntity<PipelineResponseDTO> create(@RequestBody CreatePipelineRequestDTO2 request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(dealPipelineService.createPipeline(request));
    }

    @GetMapping
    public ResponseEntity<Page<PipelineResponseDTO>> listAll(PipelineFilterDTO filter, @PageableDefault(size = AppConstants.DEFAULT_SIZE, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(dealPipelineService.getAllPipeline(filter, pageable));
    }

    @GetMapping("/{pipelineId}")
    public ResponseEntity<PipelineResponseDTO> get(@PathVariable Integer pipelineId) {
        return ResponseEntity.ok(dealPipelineService.getPipeline(pipelineId));
    }

    @PatchMapping("/{pipelineId}")
    public ResponseEntity<PipelineResponseDTO> update(@PathVariable Integer pipelineId, @Valid @RequestBody UpdatePipelineRequestDTO request) {
        return ResponseEntity.ok(dealPipelineService.updatePipelineById(pipelineId, request));
    }

//    @DeleteMapping("/{pipelineId}")
//    public ResponseEntity<Void> inactivate(@PathVariable Integer pipelineId) {
//        pipelineService.inactivatePipelineById(pipelineId);
//        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
//    }

    @PostMapping("/{pipelineId}/migrate-and-inactivate")
    public ResponseEntity<Void> migrateAndInactivate(@PathVariable Integer pipelineId, @Valid @RequestBody PipelineMigrationRequestDTO request) {
        dealPipelineService.migrateAndInactivate(pipelineId, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
