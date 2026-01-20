package com.b2b.b2b.modules.crm.pipeline.controller;

import com.b2b.b2b.modules.crm.pipeline.payloads.CreatePipelineRequestDTO;
import com.b2b.b2b.modules.crm.pipeline.payloads.PipelineMigrationRequestDTO;
import com.b2b.b2b.modules.crm.pipeline.payloads.PipelineResponseDTO;
import com.b2b.b2b.modules.crm.pipeline.payloads.UpdatePipelineRequestDTO;
import com.b2b.b2b.modules.crm.pipeline.service.MigrationService;
import com.b2b.b2b.modules.crm.pipeline.service.PipelineService;
import com.b2b.b2b.shared.AuthUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("app/v1/pipelines")
@RequiredArgsConstructor
public class PipelineController {

    private final AuthUtil authUtil;
    private final PipelineService pipelineService;
    private final MigrationService migrationService;

    @PostMapping
    public ResponseEntity<PipelineResponseDTO> create(@RequestBody CreatePipelineRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pipelineService.createPipeline(request));
    }

    @GetMapping
    public ResponseEntity<List<PipelineResponseDTO>> listAll() {
        return ResponseEntity.ok(pipelineService.getAllPipeline());
    }

    @GetMapping("/{pipelineId}")
    public ResponseEntity<PipelineResponseDTO> get(@PathVariable Integer pipelineId) {
        return ResponseEntity.ok(pipelineService.getPipelineById(pipelineId));
    }

    @PatchMapping("/{pipelineId}")
    public ResponseEntity<PipelineResponseDTO> update(@PathVariable Integer pipelineId, @Valid @RequestBody UpdatePipelineRequestDTO request) {
        return ResponseEntity.ok(pipelineService.updatePipelineById(pipelineId, request));
    }

    @DeleteMapping("/{pipelineId}")
    public ResponseEntity<Void> inactivate(@PathVariable Integer pipelineId) {
        pipelineService.inactivatePipelineById(pipelineId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/{pipelineId}/migrate-and-inactivate")
    public ResponseEntity<Void> migrateAndInactivate(@PathVariable Integer pipelineId, @Valid @RequestBody PipelineMigrationRequestDTO request) {
        migrationService.migrateAndInactivate(pipelineId, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
