package com.b2b.b2b.modules.crm.company.repository;

import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.crm.company.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Integer>
{
    Company findByCompanyName(String companyName);
    Company findByCompanyNameAndOrganization(String companyName, Organization organization);

}
