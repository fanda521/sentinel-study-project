package com.example.study.sentinelorigin.config;

import com.alibaba.csp.sentinel.adapter.servlet.CommonFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.DispatcherType;

/**
 * 手动注册 Sentinel CommonFilter，确保 Web 请求被 Sentinel 拦截
 */
@Configuration
public class SentinelFilterConfig {

    @Bean
    public FilterRegistrationBean<CommonFilter> sentinelCommonFilterRegistration() {
        FilterRegistrationBean<CommonFilter> registrationBean = new FilterRegistrationBean<>();
        // 注册 Sentinel CommonFilter
        registrationBean.setFilter(new CommonFilter());
        // 拦截所有请求（/* 表示拦截所有路径，确保所有接口都被 Sentinel 处理）
        registrationBean.addUrlPatterns("/*");
        // 设置过滤器顺序（优先级高于其他过滤器，确保先被执行）
        registrationBean.setOrder(1);
        // 匹配所有请求分发类型（包括直接请求、转发、包含等）
        registrationBean.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.FORWARD);
        // 启用该过滤器
        registrationBean.setEnabled(true);
        return registrationBean;
    }
}