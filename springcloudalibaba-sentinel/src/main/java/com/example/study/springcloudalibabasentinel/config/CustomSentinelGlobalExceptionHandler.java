package com.example.study.springcloudalibabasentinel.config;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.*;

/**
 * Sentinel 全局异常处理器（支持 Query、路径变量、Form、JSON RequestBody 通用参数收集）
 */
@Component
public class CustomSentinelGlobalExceptionHandler implements BlockExceptionHandler {

    // 注入 Spring 内置的 ObjectMapper（推荐，避免重复创建）
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, BlockException e) throws Exception {
        // 1. 构建响应结果对象
        Map<String, Object> result = new HashMap<>(16);

        // 2. 获取请求基础信息
        String requestPath = request.getRequestURI();
        String requestMethod = request.getMethod();

        // 3. 核心：收集所有通用参数（Query、路径变量、Form、JSON RequestBody）
        Map<String, Object> allRequestParams = new HashMap<>(8);

        // 3.1 收集 Query 参数 + Form 表单参数（application/x-www-form-urlencoded）
        Map<String, String[]> queryParams = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : queryParams.entrySet()) {
            String paramName = entry.getKey();
            String[] paramValues = entry.getValue();
            allRequestParams.put(paramName, paramValues.length == 1 ? paramValues[0] : paramValues);
        }

        // 3.2 收集路径变量
        collectPathVariables(request, allRequestParams);

        // 3.3 收集 JSON RequestBody 参数（新增：从缓存请求对象中读取）
        collectJsonBodyParams(request, allRequestParams);

        // 4. 区分 BlockException 具体子类型
        String errorType = "";
        String errorMsg = "";
        HttpStatus httpStatus = HttpStatus.TOO_MANY_REQUESTS;

        if (e instanceof FlowException) {
            errorType = "FLOW_LIMITING";
            errorMsg = "请求过于频繁，触发流量控制（限流）";
            httpStatus = HttpStatus.TOO_MANY_REQUESTS; // 429
        } else if (e instanceof DegradeException) {
            errorType = "DEGRADE";
            errorMsg = "服务熔断降级，暂无法提供服务（依赖服务异常或响应缓慢）";
            httpStatus = HttpStatus.SERVICE_UNAVAILABLE; // 503
        } else if (e instanceof AuthorityException) {
            errorType = "AUTHORITY_DENY";
            errorMsg = "请求被拒绝，不符合授权规则（黑白名单限制）";
            httpStatus = HttpStatus.FORBIDDEN; // 403
        } else if (e instanceof SystemBlockException) {
            errorType = "SYSTEM_PROTECT";
            errorMsg = "系统负载过高，触发全局保护，拒绝当前请求";
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR; // 500
        }

        // 5. 填充完整响应数据
        result.put("code", httpStatus.value());
        result.put("errorType", errorType);
        result.put("errorMsg", errorMsg);
        result.put("requestPath", requestPath);
        result.put("requestMethod", requestMethod);
        result.put("allRequestParams", allRequestParams); // 包含 JSON 请求体参数
        result.put("timestamp", System.currentTimeMillis());

        // 6. 配置响应头，返回 JSON 数据
        response.setStatus(httpStatus.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        PrintWriter printWriter = response.getWriter();
        String s = objectMapper.writeValueAsString(result);
        System.out.println("响应结果：" + s);
        printWriter.write(s);
        printWriter.flush();
        printWriter.close();
    }

    /**
     * 辅助方法：收集路径变量（使用 Spring MVC 原生 API，抛弃 SentinelWebServletRequest，兼容所有版本）
     * @param request        HTTP 请求对象
     * @param allRequestParams 存储所有请求参数的 Map
     */
    private void collectPathVariables(HttpServletRequest request, Map<String, Object> allRequestParams) {
        try {
            // 方案1：通过 Spring MVC 原生 HandlerMapping 获取路径变量（推荐，最稳定）
            Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(
                    HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE
            );

            if (pathVariables != null && !pathVariables.isEmpty()) {
                // 将路径变量整合到通用参数中（自动兼容 /hello/{id}/{name} 等多路径变量）
                allRequestParams.putAll(pathVariables);
                return;
            }

            // 方案2：备用方案 - 通过 RequestContextHolder 获取请求属性（防止方案1失效）
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes != null) {
                Map<String, String> backupPathVariables = (Map<String, String>) requestAttributes.getAttribute(
                        HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE,
                        RequestAttributes.SCOPE_REQUEST
                );
                if (backupPathVariables != null && !backupPathVariables.isEmpty()) {
                    allRequestParams.putAll(backupPathVariables);
                }
            }
        } catch (Exception ex) {
            // 捕获任意异常（类型转换、属性不存在等），不影响其他参数收集
            // 无需打印日志，路径变量收集失败仅缺失该部分参数，不影响核心异常处理
        }
    }

    /**
     * 新增：收集 JSON RequestBody 参数（从缓存的请求对象中读取）
     */
    private void collectJsonBodyParams(HttpServletRequest request, Map<String, Object> allRequestParams) {
        try {
            // 判断请求是否为我们包装的可重复读取请求
            if (request instanceof CachedBodyHttpServletRequest) {
                CachedBodyHttpServletRequest cachedRequest = (CachedBodyHttpServletRequest) request;
                String cachedBody = cachedRequest.getCachedBodyAsString();

                // 非空 JSON 数据才进行解析
                if (cachedBody != null && !cachedBody.trim().isEmpty()) {
                    // 将 JSON 字符串反序列化为 Map，整合到通用参数中
                    Map<String, Object> jsonParams = objectMapper.readValue(cachedBody, Map.class);
                    allRequestParams.putAll(jsonParams);
                }
            }
        } catch (Exception ex) {
            // JSON 解析失败、非 JSON 格式等异常，直接跳过，不影响其他参数收集
        }
    }
}