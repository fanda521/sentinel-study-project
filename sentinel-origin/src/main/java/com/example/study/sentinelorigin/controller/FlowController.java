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


    // 步骤 2：注解式测试接口
    @GetMapping("/limitApp")
    @SentinelResource(
            value = CommonConstant.LIMIT_APP_RESOURCE,
            blockHandlerClass = FlowHandler.class,
            blockHandler = "limitAppBlockHandler"
    )
    public String testLimitApp() throws InterruptedException {
        Thread.sleep(2000);
        String format = String.format("【成功】当前线程：%s，请求正常执行", Thread.currentThread().getName());
        System.out.println(format);
        return format;
    }



    // 接口 1：测试 直接拒绝 效果
    @GetMapping("/strategy/default")
    @SentinelResource(
            value = CommonConstant.DEFAULT_RESOURCE,
            blockHandlerClass = FlowHandler.class,
            blockHandler = "defaultBlockHandler"
    )
    public String testDefaultControlBehavior() {
        String format = String.format("【直接拒绝-成功】当前线程：%s，请求正常执行", Thread.currentThread().getName());
        System.out.println(format);
        return format;

    }

    // 接口 2：测试 预热/冷启动 效果
    @GetMapping("/strategy/warmup")
    @SentinelResource(
            value = CommonConstant.WARM_UP_RESOURCE,
            blockHandlerClass = FlowHandler.class,
            blockHandler = "warmUpBlockHandler"
    )
    public String testWarmUpControlBehavior() {
        String format = String.format("【预热-成功】当前线程：%s，请求正常执行", Thread.currentThread().getName());
        System.out.println(format);
        return format;
    }

    // 接口 3：测试 匀速排队 效果
    @GetMapping("/strategy/ratelimiter")
    @SentinelResource(
            value = CommonConstant.RATE_LIMITER_RESOURCE,
            blockHandlerClass = FlowHandler.class,
            blockHandler = "rateLimiterBlockHandler"
    )
    public String testRateLimiterControlBehavior() {
        String format = String.format("【匀速排队-成功】当前线程：%s，请求正常执行", Thread.currentThread().getName());
        System.out.println(format);
        return format;

    }
}
