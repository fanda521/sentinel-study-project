package com.example.study.sentinelorigin.handle;

import com.alibaba.csp.sentinel.slots.block.BlockException;

/**
 * @author lucksoul
 * @version 1.0
 * @date 2026/1/10 0:50
 */
public class DegradeHandler {

    // ========== 熔断降级方法（熔断触发时调用） ==========
    public static String rtDegradeBlockHandler(BlockException e) {
        String format = String.format("【RT 熔断-触发】线程：%s，平均响应时间超过阈值，进入熔断窗口", Thread.currentThread().getName());
        System.out.println(format);
        return format;
    }

    // ========== 业务异常兜底方法（可选，非熔断触发） ==========
    public static String rtDegradeFallback(Throwable e) {
        String format = String.format("【RT 熔断-兜底】线程：%s，业务执行异常：%s", Thread.currentThread().getName(), e.getMessage());
        System.out.println(format);
        return format;
    }



    // ========== 熔断降级方法 ==========
    public static String ratioDegradeBlockHandler(BlockException e) {
        String format = String.format("【异常比例-触发】线程：%s，异常比例超过阈值，进入熔断窗口", Thread.currentThread().getName());
        System.out.println(format);
        return format;
    }

    // ========== 业务异常兜底方法 ==========
    public static String ratioDegradeFallback(Throwable e) {
        String format = String.format("【异常比例-兜底】线程：%s，业务执行异常：%s", Thread.currentThread().getName(), e.getMessage());
        System.out.println(format);
        return format;
    }


    // ========== 熔断降级方法 ==========
    public static String countDegradeBlockHandler(BlockException e) {
        String format = String.format("【异常数-触发】线程：%s，异常数超过阈值，进入熔断窗口", Thread.currentThread().getName());
        System.out.println(format);
        return format;
    }

    // ========== 业务异常兜底方法 ==========
    public static String countDegradeFallback(Throwable e) {
        String format = String.format("【异常数-兜底】线程：%s，业务执行异常：%s", Thread.currentThread().getName(), e.getMessage());
        System.out.println(format);
        return format;
    }
}
