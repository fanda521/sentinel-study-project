package com.example.study.sentinelorigin.handle;

import com.alibaba.csp.sentinel.slots.block.BlockException;

/**
 * @author lucksoul
 * @version 1.0
 * @date 2026/1/9 16:49
 */
public class FlowHandler {

    // 步骤 3：注解指定的降级方法（blockHandler 要求）
    /**
     * 1. 方法权限：public（必须）
     * 2. 返回值：与原方法一致（必须）
     * 3. 参数：与原方法一致 + 末尾追加 BlockException（必须）
     * 4. 若原方法无异常抛出，降级方法可仅追加 BlockException
     */
    public static String threadFlowBlockHandler(BlockException e) {
        return String.format("【降级】当前线程：%s，并发线程数超过阈值 5，拒绝访问", Thread.currentThread().getName());
    }

    public static String limitAppBlockHandler(BlockException e) {
        String format = String.format("【降级】当前线程：%s，请求过于频繁，触发 limitApp 限流", Thread.currentThread().getName());
        System.out.println(format);
        return format;

    }


    // 降级方法：直接拒绝
    public static String defaultBlockHandler(BlockException e) {
        String format = String.format("【直接拒绝-降级】当前线程：%s，QPS 超过阈值 10，触发限流", Thread.currentThread().getName());
        System.out.println(format);
        return format;
    }

    // 降级方法：预热
    public static String warmUpBlockHandler(BlockException e) {
        String format = String.format("【预热-降级】当前线程：%s，预热期内 QPS 超过当前阈值，触发限流", Thread.currentThread().getName());
        System.out.println(format);
        return format;
    }

    // 降级方法：匀速排队
    public static String rateLimiterBlockHandler(BlockException e) {
        String format = String.format("【匀速排队-降级】当前线程：%s，排队时间超过 1 秒，触发限流", Thread.currentThread().getName());
        System.out.println(format);
        return format;
    }
}
