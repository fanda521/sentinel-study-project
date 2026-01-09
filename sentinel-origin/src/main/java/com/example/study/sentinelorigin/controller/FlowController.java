package com.example.study.sentinelorigin.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.example.study.sentinelorigin.constant.CommonConstant;
import com.example.study.sentinelorigin.handle.FlowHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author lucksoul
 * @version 1.0
 * @date 2026/1/9 16:34
 * 限流测试
 */
@RestController
@RequestMapping("/flow")
public class FlowController {


    // 步骤 1：初始化【并发线程数】流控规则（核心配置不变）


    // 步骤 2：注解式测试接口（@SentinelResource 核心注解）
    @GetMapping("/annoThread")
    @SentinelResource(
            value = CommonConstant.THREAD_RESOURCE_NAME, // 绑定资源名，与流控规则中的资源名一致
            blockHandlerClass = FlowHandler.class,
            blockHandler = "threadFlowBlockHandler" // 指定限流降级方法（局部）
    )
    public String testAnnotationThreadFlow() throws InterruptedException {
        // 模拟耗时业务（睡眠 3 秒，让线程堆积，方便触发并发线程数限流）
        TimeUnit.SECONDS.sleep(3);

        // 正常响应结果
        return String.format("【成功】当前线程：%s，业务执行完成", Thread.currentThread().getName());
    }



}
