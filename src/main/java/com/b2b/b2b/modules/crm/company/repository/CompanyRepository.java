package com.b2b.b2b.modules.crm.company.repository;

import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.crm.company.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Integer>
{
    List<Company> findAllByOrganization(Organization org);
    Optional<Company> findByCompanyNameAndOrganization(String name, Organization org);
    Optional<Company> findByIdAndOrganization(Integer id, Organization org);
    Boolean existsByCompanyNameAndOrganization(String name, Organization org);


}
