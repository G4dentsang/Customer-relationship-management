package com.b2b.b2b.modules.crm.pipeline.controller;

import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.pipeline.payloads.CreatePipelineRequestDTO;
import com.b2b.b2b.modules.crm.pipeline.payloads.PipelineResponseDTO;
import com.b2b.b2b.modules.crm.pipeline.service.PipelineService;
import com.b2b.b2b.shared.AuthUtil;
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

    @PostMapping
    public ResponseEntity<PipelineResponseDTO> create(@RequestBody CreatePipelineRequestDTO request) {
        User user = authUtil.loggedInUser();
        return ResponseEntity.status(HttpStatus.CREATED).body(pipelineService.createPipeline(request, user));
    }

    @GetMapping
    public ResponseEntity<List<PipelineResponseDTO>> get() {
        User user = authUtil.loggedInUser();
        return ResponseEntity.ok(pipelineService.getAllPipeline(user));
    }

    @GetMapping("/{pipelineId}")
    public ResponseEntity<?> getPipelineById(@PathVariable Integer pipelineId) {
        User user = authUtil.loggedInUser();
        return ResponseEntity.ok(pipelineService.getPipelineById(pipelineId, user));
    }
}
