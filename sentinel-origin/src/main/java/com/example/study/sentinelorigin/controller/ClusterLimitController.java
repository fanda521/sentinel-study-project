package com.example.study.sentinelorigin.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 集群限流保护的业务接口
 */
@RestController
public class ClusterLimitController {
    // 集群限流保护的资源名
    public static final String CLUSTER_RESOURCE = "clusterProductResource";

    /**
     * 商品查询接口，受集群限流保护
     */
    @SentinelResource(
            value = CLUSTER_RESOURCE,
            blockHandler = "clusterBlockHandler"
    )
    @GetMapping("/cluster/product/query")
    public String queryProduct(@RequestParam("productId") String productId) {
        String format = String.format("【集群限流-成功】productId：%s，集群总流量未超限", productId);
        System.out.println(format);
        return format;
    }

    /**
     * 集群限流降级方法
     */
    public String clusterBlockHandler(String productId, BlockException e) {
        String format = String.format("【集群限流-触发】productId：%s，集群总流量超限", productId);
        System.out.println(format);
        return format;
    }
}