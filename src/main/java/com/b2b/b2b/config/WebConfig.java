package com.b2b.b2b.config;

import com.b2b.b2b.shared.multitenancy.OrganizationInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class WebConfig implements WebMvcConfigurer
{
    private final OrganizationInterceptor organizationInterceptor;

    public WebConfig(OrganizationInterceptor organizationInterceptor) {
        this.organizationInterceptor = organizationInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
       registry.addInterceptor(organizationInterceptor)
               .addPathPatterns("/api/v1/**")
               .excludePathPatterns("/api/v1/auth/**");
    }
}
