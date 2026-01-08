package com.example.study.sentinelorigin.handle;

import com.alibaba.csp.sentinel.slots.block.BlockException;

/**
 * @author lucksoul
 * @version 1.0
 * @date 2026/1/9 2:54
 */
public class FailBlockHandler {

    public static String failBlockTest01(BlockException e) {
        e.printStackTrace();
        System.out.println("failBlockHandler-限流");
        return "failBlockHandler-限流";
    }
}
