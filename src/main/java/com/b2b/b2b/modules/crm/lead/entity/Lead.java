package com.b2b.b2b.modules.crm.lead.entity;

import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.crm.company.entity.Company;
import com.b2b.b2b.modules.crm.deal.entity.Deals;
import com.b2b.b2b.modules.crm.pipeline.entity.Pipeline;
import com.b2b.b2b.modules.crm.pipelineStage.entity.PipelineStage;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Lead {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String leadName;
    private String leadEmail;
    private String leadPhone;
    @Enumerated(EnumType.STRING)
    private LeadStatus leadStatus = LeadStatus.NEW;
    private LocalDateTime createdAt;

    @ManyToOne
    private Organization organization;

    @ManyToOne
    private Company company;

    @ManyToOne
    private Pipeline pipeline;

    @ManyToOne
    private PipelineStage pipelineStage;

    @OneToMany(mappedBy = "lead")
    private List<Deals> deals = new ArrayList<>();

    public Lead(String leadEmail, String leadName, String leadPhone) {
        this.leadEmail = leadEmail;
        this.leadName = leadName;
        this.leadPhone = leadPhone;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }


}
