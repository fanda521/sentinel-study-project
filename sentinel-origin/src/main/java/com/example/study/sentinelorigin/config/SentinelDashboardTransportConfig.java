package com.example.study.sentinelorigin.config;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 原生 Sentinel 手动编码配置 Dashboard 通信（替代 VM 参数）
 */
@Component
public class SentinelDashboardTransportConfig {
    @PostConstruct
    public void setSentinelTransportConfig() {
        // 1. 配置 Dashboard 地址
        System.setProperty("csp.sentinel.dashboard.server", "127.0.0.1:8080");
        // 2. 配置通信端口（若被占用，可修改为 8720 等）
        System.setProperty("csp.sentinel.transport.port", "8719");
        // 3. 配置客户端 IP（单机环境必配）
        System.setProperty("csp.sentinel.transport.client.ip", "127.0.0.1");
        System.setProperty("project.name", "sentinel-origin");

        System.out.println("===== 原生 Sentinel Dashboard 通信配置完成 =====");
    }
}