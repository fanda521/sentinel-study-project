package com.example.study.sentinelorigin.config;

import com.alibaba.csp.sentinel.adapter.servlet.CommonFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.DispatcherType;
import java.util.HashMap;
import java.util.Map;

/**
 * 手动注册 Sentinel CommonFilter，确保 Web 请求被 Sentinel 拦截
 */
@Configuration
public class SentinelFilterConfig {

    @Bean
    public FilterRegistrationBean<CommonFilter> sentinelCommonFilterRegistration() {
        FilterRegistrationBean<CommonFilter> registrationBean = new FilterRegistrationBean<>();


        // 关键配置：关闭 Web 链路统一合并，让 Sentinel 识别自定义入口链路
        Map<String, String> initParameters = new HashMap<>();
        initParameters.put(CommonFilter.WEB_CONTEXT_UNIFY, "false"); // 关闭链路合并（默认 true）
        registrationBean.setInitParameters(initParameters);
        // 注册 Sentinel CommonFilter
        registrationBean.setFilter(new CommonFilter());
        // 拦截所有请求（/* 表示拦截所有路径，确保所有接口都被 Sentinel 处理）
        registrationBean.addUrlPatterns("/*");
        // 3. 确保过滤器优先级最高，先于 Spring MVC 拦截器执行
        registrationBean.setOrder(Integer.MIN_VALUE);

        // 4. 确保拦截所有请求分发类型
        registrationBean.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.FORWARD,
                DispatcherType.INCLUDE, DispatcherType.ERROR);
        // 启用该过滤器
        registrationBean.setEnabled(true);
        return registrationBean;
    }
}