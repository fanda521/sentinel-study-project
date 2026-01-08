package com.example.study.sentinelorigin.config;

import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lucksoul
 * @version 1.0
 * @date 2026/1/9 2:39
 */
@Configuration
public class SentinelSourceConfig {

    @Bean
    public SentinelResourceAspect sentinelResourceAspect() {
        return new SentinelResourceAspect();
    }
}
