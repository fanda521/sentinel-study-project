package com.example.study.sentinelorigin.config;

import com.alibaba.csp.sentinel.adapter.servlet.callback.RequestOriginParser;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 自定义请求来源解析器：从请求头中提取 "X-Sentinel-App" 作为应用来源（limitApp）
 */
@Component
public class CustomRequestOriginParser implements RequestOriginParser {

    public CustomRequestOriginParser() {
        System.out.println("========== CustomRequestOriginParser 被 Spring 实例化了 ==========");
    }

    @Override
    public String parseOrigin(HttpServletRequest request) {
        // 从请求头中获取应用来源（可改为从请求参数、Cookie 等提取）
        String appName = request.getHeader("X-Sentinel-App");
        // 若请求头中无该字段，默认返回 "unknown"
        System.out.println("请求来源：" + appName);
        return appName == null ? "unknown" : appName;
    }
}