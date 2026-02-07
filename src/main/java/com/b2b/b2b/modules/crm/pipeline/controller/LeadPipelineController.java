package com.b2b.b2b.modules.crm.pipeline.controller;

import com.b2b.b2b.config.AppConstants;
import com.b2b.b2b.modules.crm.pipeline.payloads.*;
import com.b2b.b2b.modules.crm.pipeline.service.LeadPipelineService;
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
@RequestMapping("app/v1/leads/pipelines")
@RequiredArgsConstructor
public class LeadPipelineController {

    private final LeadPipelineService leadPipelineService;

    @PostMapping
    public ResponseEntity<PipelineResponseDTO> create(@RequestBody CreatePipelineRequestDTO2 request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(leadPipelineService.createPipeline(request));
    }

    @GetMapping
    public ResponseEntity<Page<PipelineResponseDTO>> listAll(PipelineFilterDTO filter, @PageableDefault(size = AppConstants.DEFAULT_SIZE, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(leadPipelineService.getAllPipeline(filter, pageable));
    }

    @GetMapping("/{pipelineId}")
    public ResponseEntity<PipelineResponseDTO> get(@PathVariable Integer pipelineId) {
        return ResponseEntity.ok(leadPipelineService.getPipeline(pipelineId));
    }

    @PatchMapping("/{pipelineId}")
    public ResponseEntity<PipelineResponseDTO> update(@PathVariable Integer pipelineId, @Valid @RequestBody UpdatePipelineRequestDTO request) {
        return ResponseEntity.ok(leadPipelineService.updatePipelineById(pipelineId, request));
    }

//    @DeleteMapping("/{pipelineId}")
//    public ResponseEntity<Void> inactivate(@PathVariable Integer pipelineId) {
//        leadPipOperations.inactivatePipelineById(pipelineId);
//        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
//    }

    @PostMapping("/{pipelineId}/migrate-and-inactivate")
    public ResponseEntity<Void> migrateAndInactivate(@PathVariable Integer pipelineId, @Valid @RequestBody PipelineMigrationRequestDTO request) {
        leadPipelineService.migrateAndInactivate(pipelineId, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
