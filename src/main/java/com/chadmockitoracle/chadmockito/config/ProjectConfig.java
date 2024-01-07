package com.chadmockitoracle.chadmockito.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

@Component
public class ProjectConfig {

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

}
