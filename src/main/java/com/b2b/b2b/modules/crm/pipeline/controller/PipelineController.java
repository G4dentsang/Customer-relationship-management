package com.b2b.b2b.modules.crm.pipeline.controller;

import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.pipeline.payloads.CreatePipelineRequestDTO;
import com.b2b.b2b.modules.crm.pipeline.payloads.PipelineResponseDTO;
import com.b2b.b2b.modules.crm.pipeline.service.PipelineService;
import com.b2b.b2b.shared.AuthUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("app/v1/pipelines")
public class PipelineController {
    private final AuthUtil authUtil;
    private final PipelineService pipelineService;
    public  PipelineController(AuthUtil authUtil, PipelineService pipelineService) {
        this.authUtil = authUtil;
        this.pipelineService = pipelineService;
    }
    @PostMapping("")
    public ResponseEntity<?> createPipeline(@RequestBody CreatePipelineRequestDTO createPipelineRequestDTO) {
        User user = authUtil.loggedInUser();
        PipelineResponseDTO savedPipelineResponseDTO = pipelineService.createPipeline(createPipelineRequestDTO, user);

        return new ResponseEntity<>(savedPipelineResponseDTO,HttpStatus.CREATED);
    }
}
