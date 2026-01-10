package com.example.study.sentinelorigin.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.example.study.sentinelorigin.constant.CommonConstant;
import com.example.study.sentinelorigin.handle.HotParamHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lucksoul
 * @version 1.0
 * @date 2026/1/10 18:31
 */

@RestController
@RequestMapping("/hotParam")
public class HotParamController {


    /**
     * 基础热点参数限流：对 productId（第 0 个参数）限流
     */
    @SentinelResource(
            value = CommonConstant.HOT_PARAM_BASIC_RESOURCE,
            blockHandlerClass = HotParamHandler.class,
            blockHandler = "hotParamBlockHandler" // 限流降级方法
    )
    @GetMapping("/product/query")
    public String queryProduct(@RequestParam("productId") String productId) {
        String format = String.format("【成功】查询商品，productId：%s，请求正常处理", productId);
        System.out.println(format);
        return format;
    }


    /**
     * 当 orderId 的 QPS 超过 3 → 触发限流；
     * 当 userId 的 QPS 超过 5 → 触发限流；
     * 两个参数的 QPS 统计相互独立。
     * @param orderId
     * @param userId
     * @return
     */
    @SentinelResource(
            value = CommonConstant.HOT_PARAM_MULTI_RESOURCE,
            blockHandlerClass = HotParamHandler.class,
            blockHandler = "multiParamBlockHandler"
    )
    @GetMapping("/order/query")
    public String queryOrder(
            @RequestParam("orderId") Long orderId,
            @RequestParam("userId") String userId
    ) {
        String format = String.format("【成功】查询订单，orderId：%s，userId：%s", orderId, userId);
        System.out.println(format);
        return format;
    }


}
