package com.b2b.b2b.modules.crm.pipelineStage.entity;

import com.b2b.b2b.modules.crm.deal.entity.Deals;
import com.b2b.b2b.modules.crm.pipeline.entity.Pipelines;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PipelineStages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String stageName;
    private String stageDescription;
    private int stageOrder;
    @ManyToOne
    private Pipelines pipeline;
    @OneToMany(mappedBy = "pipelineStages")
    private List<Deals> deals;
    //workflow rules relation later during workflow module
}
