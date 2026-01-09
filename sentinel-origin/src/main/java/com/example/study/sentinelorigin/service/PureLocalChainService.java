package com.example.study.sentinelorigin.service;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.springframework.stereotype.Service;

@Service
public class PureLocalChainService {

    // 常量定义（资源名必须唯一）
    public static final String CHAIN_ENTRY = "pureLocalEntry";
    public static final String CHAIN_CURRENT = "pureLocalCurrent";

    // ========== 入口资源 ==========
    @SentinelResource(value = CHAIN_ENTRY)
    public String entryMethod() {
        // 核心：入口资源 直接调用 当前资源（纯本地方法调用，无 Web 干扰）
        return currentMethod();
    }

    // ========== 当前资源（被保护的资源） ==========
    @SentinelResource(
            value = CHAIN_CURRENT,
            blockHandler = "currentBlockHandler" // 内联降级方法，避免外部类问题
    )
    public String currentMethod() {
        String format = String.format("【成功】线程：%s，当前资源执行", Thread.currentThread().getName());
        System.out.println(format);
        return format;
    }

    // 降级方法（必须 public，参数最后加 BlockException）
    public String currentBlockHandler(BlockException e) {
        String format = String.format("【降级】线程：%s，链路流控触发", Thread.currentThread().getName());
        System.out.println(format);
        return format;
    }
}