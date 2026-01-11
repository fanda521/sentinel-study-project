package com.example.study.springcloudalibabasentinel.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    /**
     * 被 Sentinel 保护的接口资源（兼容 JDK 8 + Spring Boot 2.7.5）
     * @param id 路径参数
     * @return 响应结果
     */
    @GetMapping("/hello/{id}")
    @SentinelResource(
            value = "helloResource", // 资源名称（控制台标识）
            blockHandler = "helloBlockHandler", // 限流/熔断兜底方法
            fallback = "helloFallback" // 业务异常兜底方法
    )
    public String hello(@PathVariable Integer id) {
        // 模拟业务异常（JDK 8 支持的语法）
        if (id == 0) {
            throw new RuntimeException("id 不能为 0（JDK 8 兼容）");
        }
        String s = "Hello Sentinel 1.8.6 + Spring Boot 2.7.5, id: " + id;
        System.out.println(s);
        return s;
    }

    /**
     * 限流/熔断兜底方法（JDK 8 兼容，支持 BlockException 入参）
     */
    public String helloBlockHandler(Integer id, BlockException e) {
        String s = "当前请求过于频繁，请稍后再试（限流/熔断保护），id: " + id;
        System.out.println(s);
        return s;
    }

    /**
     * 业务异常兜底方法（JDK 8 兼容，支持 Throwable 入参）
     */
    public String helloFallback(Integer id, Throwable e) {
        String s = "业务处理失败：" + e.getMessage() + "，id: " + id;
        System.out.println(s);
        return s;
    }
}