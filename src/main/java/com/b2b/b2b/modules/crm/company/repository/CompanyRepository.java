package com.b2b.b2b.modules.crm.company.repository;

import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.crm.company.entity.Company;
import com.b2b.b2b.modules.crm.contact.entity.Contacts;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyRepository extends JpaRepository<Company, Integer>
{
    List<Company> findAllByOrganization(Organization organization);
    Company findByCompanyNameAndOrganization(String companyName, Organization organization);


}
