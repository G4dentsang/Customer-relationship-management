package com.b2b.b2b.modules.crm.company.repository;

import com.b2b.b2b.modules.crm.company.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Integer>
{
    Optional<Company> findByCompanyName(String name);
    Boolean existsByCompanyName(String name);


}
