package com.example.admin.config;

import com.example.admin.interceptor.LoginInterceptor;
import com.example.admin.interceptor.SuperAdminInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
    @Autowired
    private LoginInterceptor loginInterceptor;

    @Autowired
    private SuperAdminInterceptor superAdminInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/api/**")
        .excludePathPatterns("/api/login");
        registry.addInterceptor(superAdminInterceptor)
                .addPathPatterns("/api/super-admin/**");
    }

}
