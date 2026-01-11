package com.example.study.springcloudalibabasentinel.config;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 请求体缓存过滤器
 * 提前包装请求，缓存请求体数据，支持重复读取，为异常处理器收集 RequestBody 参数提供支持
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE) // 设置最高优先级，确保在 Spring MVC 和 Sentinel 之前执行
public class RequestBodyCacheFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            // 仅包装 HttpServletRequest，且仅处理 JSON 格式的请求
            if (request instanceof HttpServletRequest) {
                HttpServletRequest httpRequest = (HttpServletRequest) request;
                String contentType = httpRequest.getContentType();

                // 只对 application/json 格式的请求进行包装（避免不必要的性能开销）
                if (contentType != null && (contentType.contains("application/json")
                        || contentType.contains("application/json;charset=UTF-8"))) {
                    // 包装请求，缓存请求体
                    CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(httpRequest);
                    // 传递包装后的请求到后续过滤器链
                    chain.doFilter(cachedRequest, response);
                    return;
                }
            }

            // 非 JSON 格式请求，直接放行
            chain.doFilter(request, response);
        } catch (Exception e) {
            // 记录异常日志
            System.err.println("Filter error: " + e.getMessage());
            chain.doFilter(request, response); // 出错时也继续执行过滤器链
        }
    }

    // 初始化和销毁方法默认实现即可，无需额外逻辑
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}