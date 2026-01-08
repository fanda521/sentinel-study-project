package com.example.study.sentinelorigin.controller;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.example.study.sentinelorigin.handle.FailBlockHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lucksoul
 * @version 1.0
 * @date 2026/1/9 2:21
 */
@RestController
@RequestMapping("/failBlock")
public class FailBackAndBlockHandlerDefaultClass {


    @RequestMapping("/test01")
    @SentinelResource(value = "failBlock-test01", blockHandlerClass = FailBlockHandler.class ,blockHandler = "failBlockTest01")
    public String test01() {
        return "failBlock-test01";
    }
}
