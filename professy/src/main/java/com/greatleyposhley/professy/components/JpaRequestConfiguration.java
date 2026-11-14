package com.greatleyposhley.professy.components;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Keep normal entity serialization, but never pin a database connection to an event stream. */
@Configuration(proxyBeanMethods = false)
public class JpaRequestConfiguration {
    @Bean
    OpenEntityManagerInViewInterceptor requestEntityManager(EntityManagerFactory factory) {
        var interceptor = new OpenEntityManagerInViewInterceptor();
        interceptor.setEntityManagerFactory(factory);
        return interceptor;
    }

    @Bean
    WebMvcConfigurer entityManagerRequests(OpenEntityManagerInViewInterceptor interceptor) {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addWebRequestInterceptor(interceptor)
                        .excludePathPatterns("/api/workspace/settings/events");
            }
        };
    }
}
