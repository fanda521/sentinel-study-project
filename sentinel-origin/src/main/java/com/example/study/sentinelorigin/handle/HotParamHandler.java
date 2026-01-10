package com.example.study.sentinelorigin.handle;

import com.alibaba.csp.sentinel.slots.block.BlockException;

/**
 * @author lucksoul
 * @version 1.0
 * @date 2026/1/10 18:32
 */
public class HotParamHandler {

    /**
     * 热点参数限流降级方法
     * 要求：参数与原方法一致 + 末尾追加 BlockException
     */
    public static String hotParamBlockHandler(String productId, BlockException e) {
        String format = String.format("【限流】商品查询触发热点限流，productId：%s，原因：%s", productId, e.getClass().getSimpleName());
        System.out.println(format);
        return format;
    }

    public static String multiParamBlockHandler(Long orderId, String userId, BlockException e) {
        String format = String.format("【限流】订单查询触发热点限流，orderId：%s，userId：%s", orderId, userId);
        System.out.println(format);
        return format;
    }
}
