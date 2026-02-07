package com.b2b.b2b.modules.crm.company.persistence;

import com.b2b.b2b.modules.crm.company.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;



public interface CompanyRepository extends JpaRepository<Company, Integer>, JpaSpecificationExecutor<Company>
{
    Boolean existsByCompanyName(String name);

}
