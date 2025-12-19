package com.b2b.b2b.modules.crm.deal.repository;

import com.b2b.b2b.modules.crm.deal.entity.Deals;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DealRepository extends JpaRepository<Deals, Integer> {
}
