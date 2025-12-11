package com.b2b.b2b.modules.crm.deal.entity;

import com.b2b.b2b.modules.crm.company.entity.Company;
import com.b2b.b2b.modules.crm.lead.entity.Lead;
import com.b2b.b2b.modules.crm.pipelineStage.entity.PipelineStage;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Deals {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String dealName;
    private Double dealAmount;
    private DealStatus dealStatus = DealStatus.CREATED;
    private LocalDateTime closedAt;
    private LocalDateTime createdAt;
    @ManyToOne
    private Lead lead;
    @ManyToOne
    private Company company;
    @ManyToOne
    private PipelineStage pipelineStage;
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

}
