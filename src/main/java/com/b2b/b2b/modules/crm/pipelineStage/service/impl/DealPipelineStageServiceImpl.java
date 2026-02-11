package com.b2b.b2b.modules.crm.pipelineStage.service.impl;

import com.b2b.b2b.exception.BadRequestException;
import com.b2b.b2b.modules.crm.deal.internal.infrastructure.persistence.Deal;
import com.b2b.b2b.modules.crm.deal.internal.infrastructure.persistence.DealStatus;
import com.b2b.b2b.modules.crm.deal.internal.infrastructure.persistence.DealRepository;
import com.b2b.b2b.modules.crm.pipeline.model.DealPipeline;
import com.b2b.b2b.modules.crm.pipeline.persistence.DealPipelineRepository;
import com.b2b.b2b.modules.crm.pipelineStage.model.DealPipelineStage;
import com.b2b.b2b.modules.crm.pipelineStage.payloads.PipelineStageRequestDTO;
import com.b2b.b2b.modules.crm.pipelineStage.persistence.DealPipelineStageRepository;
import com.b2b.b2b.modules.crm.pipelineStage.service.DealPipelineStageService;
import com.b2b.b2b.modules.crm.pipelineStage.util.StageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
public class DealPipelineStageServiceImpl extends BasePipelineStageService<DealPipeline, DealPipelineStage> implements DealPipelineStageService
{
    private final DealRepository dealRepository;
    private final DealPipelineStageRepository dealPipelineStageRepository;

    public DealPipelineStageServiceImpl(DealPipelineStageRepository dealPipelineStageRepository,
                                        DealPipelineRepository pipelineRepository,
                                        StageUtils stageUtils,
                                        DealRepository dealRepository) {
        super(dealPipelineStageRepository, pipelineRepository, stageUtils);
        this.dealRepository = dealRepository;
        this.dealPipelineStageRepository = dealPipelineStageRepository;
    }

    @Override
    protected DealPipelineStage createNewStage(PipelineStageRequestDTO dto, DealPipeline pipeline) {
        DealStatus status;

        try {
            status = DealStatus.valueOf(dto.getMappedStatus());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid Deal Status: " + dto.getMappedStatus() + ". Allowed values: " + Arrays.toString(DealStatus.values()));
        }
        return new DealPipelineStage(dto.getStageName(), dto.getStageOrder(), pipeline, status);
    }

    @Override
    protected void updateStatusFromStage(DealPipelineStage stage,  String newStatus) {
        DealStatus status = DealStatus.valueOf(newStatus);
        stage.setMappedStatus(status);
    }

    @Override
    protected long countItemInStage(DealPipelineStage stage) {
        return dealRepository.countByPipelineStage(stage);
    }

    @Override
    protected void performMigration(Integer stageId, Integer targetStageId) {
        dealRepository.bulkMigrateBetweenStage(stageId, targetStageId);
    }

    @Override
    protected List<DealPipelineStage> findStagesByPipelineOrderByStageOrderAsc(DealPipeline pipeline) {
        return dealPipelineStageRepository.findAllByPipelineOrderByStageOrderAsc(pipeline);
    }

    @Override
    public void createDefaultStages(DealPipeline pipeline) {
        List<DealPipelineStage> stages = new ArrayList<>();
        stages.add(new DealPipelineStage("Discovery", 1, pipeline, DealStatus.OPEN));
        stages.add(new DealPipelineStage("Negotiated", 2, pipeline, DealStatus.OPEN));
        stages.add(new DealPipelineStage("Lost", 3, pipeline, DealStatus.CLOSED_LOST));
        stages.add(new DealPipelineStage("Won", 3, pipeline, DealStatus.CLOSED_WON));

        dealPipelineStageRepository.saveAll(stages);
    }

    @Override
    public void assignDefaultStage(DealPipeline defaultPipeline, Deal deal) {
        DealPipelineStage firstStage = defaultPipeline.getPipelineStages().stream()
                .min(Comparator.comparingInt(DealPipelineStage::getStageOrder))
                .orElseThrow(() -> new IllegalStateException("Default Deal Pipeline has no stages"));
        deal.setPipelineStage(firstStage);
    }
}
