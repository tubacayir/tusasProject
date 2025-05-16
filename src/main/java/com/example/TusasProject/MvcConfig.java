package com.example.TusasProject;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/").setViewName("home");
        registry.addViewController("/register").setViewName("register");
        registry.addViewController("/panel").setViewName("panel");
        registry.addViewController("/deneme").setViewName("scenarioPanel");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/trends").setViewName("trends");
        registry.addViewController("/create-scenario").setViewName("create-scenario");
        registry.addViewController("/show-scenario").setViewName("show-scenario");
        registry.addViewController("/save").setViewName("save");
        registry.addViewController("/published-scenarios").setViewName("published-scenarios");

    }
}

