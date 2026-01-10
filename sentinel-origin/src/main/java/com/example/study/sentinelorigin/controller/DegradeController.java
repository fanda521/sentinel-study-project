package com.example.study.sentinelorigin.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.example.study.sentinelorigin.constant.CommonConstant;
import com.example.study.sentinelorigin.handle.DegradeHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

/**
 * @author lucksoul
 * @version 1.0
 * @date 2026/1/10 0:47
 */
@RestController
@RequestMapping("/degrade")
public class DegradeController {

    private static final Random RANDOM = new Random();

    // ========== 业务方法（模拟慢响应，触发 RT 熔断） ==========
    @SentinelResource(
            value = CommonConstant.RT_DEGRADE_RESOURCE,
            blockHandlerClass = DegradeHandler.class,
            fallbackClass = DegradeHandler.class,
            blockHandler = "rtDegradeBlockHandler", // 熔断降级方法
            fallback = "rtDegradeFallback" // 业务异常兜底方法（可选）
    )
    @RequestMapping("/rt")
    public String doSlowBusiness() {
        // 模拟慢响应：睡眠 200 毫秒（超过 RT 阈值 100 毫秒）
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException("业务执行中断", e);
        }
        String format = String.format("【RT 熔断-成功】线程：%s，业务执行完成（慢响应）", Thread.currentThread().getName());
        System.out.println(format);
        return format;
    }


    // ========== 业务方法（模拟随机异常，触发异常比例熔断） ==========
    @SentinelResource(
            value = CommonConstant.RATIO_DEGRADE_RESOURCE,
            blockHandlerClass = DegradeHandler.class,
            fallbackClass = DegradeHandler.class,
            blockHandler = "ratioDegradeBlockHandler",
            fallback = "ratioDegradeFallback"
    )
    @RequestMapping("/exceptionRate")
    public String doRandomExceptionBusiness() {
        // 模拟 60% 概率抛出异常（超过异常比例阈值 0.5）
        if (RANDOM.nextDouble() > 0.4) {
            throw new RuntimeException("业务执行失败（随机异常）");
        }
        String format = String.format("【异常比例-成功】线程：%s，业务执行完成", Thread.currentThread().getName());
        System.out.println(format);
        return format;
    }


    // 异常计数器（模拟累计异常）
    private int exceptionCounter = 0;

    // ========== 业务方法（模拟累计异常，触发异常数熔断） ==========
    @SentinelResource(
            value = CommonConstant.COUNT_DEGRADE_RESOURCE,
            blockHandlerClass = DegradeHandler.class,
            fallbackClass = DegradeHandler.class,
            blockHandler = "countDegradeBlockHandler",
            fallback = "countDegradeFallback"
    )
    @RequestMapping("/exceptionCount")
    public String doAccumulateExceptionBusiness() {
        // 模拟每次调用都抛出异常，快速累计异常数（超过阈值 3）
        exceptionCounter++;
        throw new RuntimeException(String.format("业务执行失败（累计异常数：%d）", exceptionCounter));
    }


}
