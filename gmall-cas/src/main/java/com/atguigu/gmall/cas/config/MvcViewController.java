package com.atguigu.gmall.cas.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcViewController implements WebMvcConfigurer{

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
          //registry.addViewController("/login.html").setViewName("login");
    }
}
