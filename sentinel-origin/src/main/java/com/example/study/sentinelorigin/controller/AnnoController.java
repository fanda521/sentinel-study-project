package com.example.study.sentinelorigin.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lucksoul
 * @version 1.0
 * @date 2026/1/9 2:29
 */
@RestController
@RequestMapping("/anno")
public class AnnoController {
    @RequestMapping("/sayHello")
    @SentinelResource(value = "anno-hello", blockHandler = "annoHandleException")
    public String hello() {
        System.out.println("anno world");
        return "anno-hello";
    }

    public String annoHandleException(BlockException e) {
        e.printStackTrace();
        System.out.println("anno-hello-限流");
        return "anno-hello-限流";
    }

}
