package com.b2b.b2b.shared.multitenancy;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.stereotype.Component;



@Aspect
@Component
public class OrganizationFilterAspect {

    @PersistenceContext
    private EntityManager entityManager;

    @Before("execution(* com.b2b.b2b.modules.crm..service..*.*(..)) || " +
            "execution(* com.b2b.b2b.modules.workflow..service..*.*(..))")

    public void activateFilter(){
        Integer orgId = OrganizationContext.getOrgId();
        if(orgId != null){
          Session session = entityManager.unwrap(Session.class);
          Filter filter = session.enableFilter("organizationFilter");
          filter.setParameter("orgId", orgId);
        }
    }
}
