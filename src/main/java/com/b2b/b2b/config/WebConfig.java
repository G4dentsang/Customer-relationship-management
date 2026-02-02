package com.b2b.b2b.config;

import com.b2b.b2b.shared.multitenancy.OrganizationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer
{
    private final OrganizationInterceptor organizationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
       registry.addInterceptor(organizationInterceptor)
               .addPathPatterns("/**")
               .excludePathPatterns("/api/v1/auth/**")
               .excludePathPatterns("/app/v1/management/users/**");
    }
}
