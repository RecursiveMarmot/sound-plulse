package com.timess.soundpulse.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AssistantConfig {
    
    @Bean
    public ExecutorService actionExecutorPool() {
        return Executors.newFixedThreadPool(10);
    }
}