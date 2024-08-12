package com.ebidding.common.config;

import com.ebidding.common.auth.AuthFeignRequestInterceptor;
import com.ebidding.common.auth.AuthHandlerInterceptor;
import feign.RequestInterceptor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Import({GlobalExceptionHandler.class})
public class EBiddingConfig implements WebMvcConfigurer {
    @Bean
    public ModelMapper getModelMapper() {
        return new ModelMapper();
    }

    @Bean
    public RequestInterceptor getRequestInterceptor() {
        return new AuthFeignRequestInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthHandlerInterceptor());
    }

}
