package com.book.conf;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.ResourceUrlProvider;
import org.springframework.web.servlet.resource.ResourceUrlProviderExposingInterceptor;

@Configuration
public class MyWebMvcConfigurer implements WebMvcConfigurer {

    @Resource
    private MyCustomInterceptor myCustomInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 AuthInterceptor 拦截器，并设置拦截的 URL
        registry.addInterceptor(myCustomInterceptor)
                .addPathPatterns("/**")
                .addPathPatterns("/user/judgeExpire")
                .excludePathPatterns("/user/onLogin");
    }
}

