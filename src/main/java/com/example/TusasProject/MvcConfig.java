package com.example.TusasProject;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/expertLogin").setViewName("experLogin");
        registry.addViewController("/managerLogin").setViewName("managerLogin");
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/register").setViewName("register");
        registry.addViewController("/panel").setViewName("panel");
        registry.addViewController("/deneme").setViewName("deneme");


    }
}

