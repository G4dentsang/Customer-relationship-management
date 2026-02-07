package com.b2b.b2b.modules.crm.pipelineStage.service.impl;

import com.b2b.b2b.exception.BadRequestException;
import com.b2b.b2b.exception.ResourceNotFoundException;
import com.b2b.b2b.modules.crm.pipeline.entity.BasePipeline;
import com.b2b.b2b.modules.crm.pipeline.repository.BasePipelineRepository;
import com.b2b.b2b.modules.crm.pipelineStage.entity.BasePipelineStage;
import com.b2b.b2b.modules.crm.pipelineStage.payloads.PipelineStageRequestDTO;
import com.b2b.b2b.modules.crm.pipelineStage.payloads.PipelineStageResponseDTO;
import com.b2b.b2b.modules.crm.pipelineStage.repository.BasePipelineStageRepository;
import com.b2b.b2b.modules.crm.pipelineStage.service.PipelineStageOperations;
import com.b2b.b2b.modules.crm.pipelineStage.util.StageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public abstract class BasePipelineStageService<
        P extends BasePipeline,
        S extends BasePipelineStage>
        implements PipelineStageOperations<P, S>
{

    protected final BasePipelineStageRepository<S, P> stageRepository;
    protected final BasePipelineRepository<P>  pipelineRepository;
    protected final StageUtils stageUtils;

    @Override
    @Transactional
    public List<PipelineStageResponseDTO> addStage(Integer pipelineId, List<PipelineStageRequestDTO> request) {
        P pipeline = pipelineRepository.findById(pipelineId)
                .orElseThrow(() -> new ResourceNotFoundException("Pipeline", "id", pipelineId));

        List<S> existingStages = findStagesByPipelineOrderByStageOrderAsc(pipeline);

        int totalStages = existingStages.size() + request.size();

        for (PipelineStageRequestDTO dto : request) {

            S newStage = createNewStage(dto, pipeline);

            int order = Math.max(1, Math.min(dto.getStageOrder(), totalStages));
            newStage.setStageOrder(order);
            existingStages.add(newStage);
        }

        //SORT AND RE-INDEX PIPELINE STAGES
        sortAndReindex(existingStages);

        List<S> stages = stageRepository.saveAll(existingStages);

        return stages.stream().map(stageUtils::createPipelineStageResponseDTO).toList();
    }

    @Override
    @Transactional
    public List<PipelineStageResponseDTO> updateStage(Integer pipelineId, Integer stageId, PipelineStageRequestDTO request) {
        P pipeline = pipelineRepository.findById(pipelineId)
                .orElseThrow(() -> new ResourceNotFoundException("Pipeline", "id", pipelineId));

        List<S> existingStages = findStagesByPipelineOrderByStageOrderAsc(pipeline);

        S stageToUpdate = existingStages.stream()
                .filter(s -> s.getId().equals(stageId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Stage", "id", stageId));

        int newStageOrder = request.getStageOrder();
        if (newStageOrder < 1) newStageOrder = 1;
        if (newStageOrder > existingStages.size()) newStageOrder = existingStages.size();

        stageToUpdate.setStageName(request.getStageName());
        stageToUpdate.setStageOrder(newStageOrder);
        stageToUpdate.setStageDescription(request.getStageDescription());

        updateStatusFromStage(stageToUpdate, request.getMappedStatus());

        existingStages.sort(Comparator.comparingInt(S::getStageOrder)
                .thenComparing(s -> s.getId().equals(stageId) ? 0 : 1));
        reIndex(existingStages);

        List<S> stages = stageRepository.saveAll(existingStages);

        return stages.stream().map(stageUtils::createPipelineStageResponseDTO).toList();
    }

    @Override
    public List<PipelineStageResponseDTO> getAllStages(Integer pipelineId) {
        P pipeline = pipelineRepository.findById(pipelineId)
                .orElseThrow(() -> new ResourceNotFoundException("Pipeline", "id", pipelineId));
        List<S> existingStages = findStagesByPipelineOrderByStageOrderAsc(pipeline);

        return existingStages.stream().map(stageUtils::createPipelineStageResponseDTO).toList();
    }

    @Override
    @Transactional
    public List<PipelineStageResponseDTO> deleteStage(Integer pipelineId, Integer stageId, Integer targetStageId) {
        P pipeline = pipelineRepository.findById(pipelineId)
                .orElseThrow(() -> new ResourceNotFoundException("Pipeline", "id", pipelineId));

        List<S> existingStages = findStagesByPipelineOrderByStageOrderAsc(pipeline);

        S stageToDelete = existingStages.stream()
                .filter(s -> s.getId().equals(stageId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Stage", "id", stageId));

        long totalCount = countItemInStage(stageToDelete);

        if (totalCount > 0) {
            if (targetStageId == null)
                throw new BadRequestException("Cannot delete stage containing items without a target stage for migration.");
            if (stageId.equals(targetStageId))
                throw new IllegalArgumentException("Target stage cannot be the same as the deleted stage.");

            S targetStage = existingStages.stream().
                    filter(s -> s.getId().equals(targetStageId)).findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Target Stage", "id", targetStageId));

            performMigration(stageId, targetStageId);
            log.info("Migrated {} items from Stage {} to {} before deletion", totalCount, stageToDelete.getStageName(), targetStage.getStageName());

        }
        existingStages.remove(stageToDelete);
        stageRepository.delete(stageToDelete);

        reIndex(existingStages);
        List<S> stages = stageRepository.saveAll(existingStages);

        return stages.stream().map(stageUtils::createPipelineStageResponseDTO).toList();
    }

    protected abstract S createNewStage(PipelineStageRequestDTO dto, P pipeline);
    protected abstract void updateStatusFromStage(S stage, String newStatus);
    protected abstract long countItemInStage(S stage);
    protected abstract void performMigration(Integer stageId, Integer targetStageId);
    protected abstract List<S> findStagesByPipelineOrderByStageOrderAsc(P pipeline);


//    public Optional<LeadPipelineStage> findNextPipelineStage(LeadPipeline pipeline, Integer currentOrder) {
//        return pipelineStageRepository.findNextStages(pipeline,currentOrder);
//    }

//    @Override
//    @Transactional
//    public void promoteToNextStage(I entity) {
//        LeadPipelineStage oldStage = entity.getPipelineStage();
//        findNextPipelineStage(entity.getPipeline(), oldStage.getStageOrder())
//                .ifPresentOrElse(
//                        nextStage -> {
//                            entity.setPipelineStage(nextStage);
//                            domainEventPublisher.publishEvent(new LeadPipelineStageChangeEvent(entity, oldStage, nextStage));
//                            log.info("{} of id: {} moved from {} to {}",
//                                    entity.getClass().getSimpleName(), entity.getId(),
//                                    oldStage.getStageName(), nextStage.getStageName());
//                        },
//                        () -> handleEndOfPipeline(entity)
//                );
//    }
//
//    private void handleEndOfPipeline(PipelineAssignable entity) {
//        if (entity instanceof Lead lead) {
//            lead.setReadyForConversion(true);
//            log.info("Lead {} is now ready for conversion.", lead.getId());
//        }
//        else if (entity instanceof Deal deal) {
//            if(deal.getDealStatus() != DealStatus.CLOSED_WON){
//                deal.setDealStatus(DealStatus.CLOSED_WON);
//                deal.setClosedAt(LocalDateTime.now());
//            }
//            log.info("Deal {} reached the end of pipeline and is marked WON.", deal.getId());
//        }
//    }

    void sortAndReindex(List<S> stages) {
        //sorting stages and then inserting based on weight
        stages.sort(Comparator.comparingInt(S::getStageOrder)
                .thenComparing(s -> s.getId() == null ? 1 : 0));

        reIndex(stages);
    }

    // --- syncing the stage order in sequence
    void reIndex(List<S> stages) {
        for (int i = 0; i < stages.size(); i++) {
            stages.get(i).setStageOrder(i + 1);
        }
    }

}
