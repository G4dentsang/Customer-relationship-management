package com.b2b.b2b.modules.crm.pipelineStage.service.impl;

import com.b2b.b2b.exception.BadRequestException;
import com.b2b.b2b.modules.crm.lead.internal.infrastructure.persistence.Lead;
import com.b2b.b2b.modules.crm.lead.internal.infrastructure.persistence.LeadStatus;
import com.b2b.b2b.modules.crm.lead.internal.infrastructure.persistence.LeadRepository;
import com.b2b.b2b.modules.crm.pipeline.model.LeadPipeline;
import com.b2b.b2b.modules.crm.pipeline.persistence.LeadPipelineRepository;
import com.b2b.b2b.modules.crm.pipelineStage.model.LeadPipelineStage;
import com.b2b.b2b.modules.crm.pipelineStage.payloads.PipelineStageRequestDTO;
import com.b2b.b2b.modules.crm.pipelineStage.persistence.LeadPipelineStageRepository;
import com.b2b.b2b.modules.crm.pipelineStage.service.LeadPipelineStageService;
import com.b2b.b2b.modules.crm.pipelineStage.util.StageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
public class LeadPipelineStageServiceImpl extends BasePipelineStageService<LeadPipeline, LeadPipelineStage> implements LeadPipelineStageService {

    private final LeadRepository leadRepository;
    private final LeadPipelineStageRepository leadPipelineStageRepository;

    public LeadPipelineStageServiceImpl(LeadPipelineStageRepository leadPipelineStageRepository,
                                        LeadPipelineRepository pipelineRepository,
                                        StageUtils stageUtils,
                                        LeadRepository leadRepository) {

        super(leadPipelineStageRepository, pipelineRepository, stageUtils);

        this.leadRepository = leadRepository;
        this.leadPipelineStageRepository = leadPipelineStageRepository;
    }

    @Override
    protected LeadPipelineStage createNewStage(PipelineStageRequestDTO dto, LeadPipeline pipeline) {
        LeadStatus status;

        try {
            status = LeadStatus.valueOf(dto.getMappedStatus());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid Lead Status: " + dto.getMappedStatus() + ". Allowed values: " + Arrays.toString(LeadStatus.values()));
        }
        return new LeadPipelineStage(dto.getStageName(), dto.getStageOrder(), pipeline, status);
    }

    @Override
    protected void updateStatusFromStage(LeadPipelineStage stage, String newStatus) {
        LeadStatus status = LeadStatus.valueOf(newStatus);
        stage.setMappedStatus(status);
    }

    @Override
    protected long countItemInStage(LeadPipelineStage stage) {
        return leadRepository.countByPipelineStage(stage);
    }

    @Override
    protected void performMigration(Integer stageId, Integer targetStageId) {
      leadRepository.bulkMigrateBetweenStage(stageId, targetStageId);
    }

    @Override
    protected List<LeadPipelineStage> findStagesByPipelineOrderByStageOrderAsc(LeadPipeline pipeline) {
        return leadPipelineStageRepository.findAllByPipelineOrderByStageOrderAsc(pipeline);
    }

    @Override
    public void createDefaultStages(LeadPipeline pipeline) {
        List<LeadPipelineStage> stages = new ArrayList<>();
        stages.add(new LeadPipelineStage("New", 1, pipeline, LeadStatus.NEW));
        stages.add(new LeadPipelineStage("Contacted", 2, pipeline, LeadStatus.CONTACTED));
        stages.add(new LeadPipelineStage("Qualified", 2, pipeline, LeadStatus.QUALIFIED));
        stages.add(new LeadPipelineStage("Lost", 3, pipeline, LeadStatus.LOST));
        stages.add(new LeadPipelineStage("Converted", 3, pipeline, LeadStatus.CONVERTED));

        stageRepository.saveAll(stages);
    }

    @Override
    public void assignDefaultStage( LeadPipeline defaultPipeline , Lead  lead) {
        LeadPipelineStage firstStage = defaultPipeline.getPipelineStages().stream()
                .min(Comparator.comparingInt(LeadPipelineStage::getStageOrder))
                .orElseThrow(() -> new IllegalStateException("Default Lead Pipeline has no stages"));

        lead.setPipelineStage(firstStage);
    }
}
