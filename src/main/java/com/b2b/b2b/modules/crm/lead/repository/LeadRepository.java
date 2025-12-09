package com.b2b.b2b.modules.crm.lead.repository;

import com.b2b.b2b.modules.crm.lead.entity.Lead;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeadRepository extends JpaRepository<Lead, Integer> {
}
