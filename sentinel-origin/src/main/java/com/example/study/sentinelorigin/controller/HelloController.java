package com.example.study.sentinelorigin.controller;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lucksoul
 * @version 1.0
 * @date 2026/1/9 1:22
 */
@RestController
@RequestMapping("/hello")
public class HelloController {


    @RequestMapping("/sayHello")
    public String hello() {

        Entry entry = null;
        // 务必保证finally会被执行
        try {
            // 资源名可使用任意有业务语义的字符串
            entry = SphU.entry("hello");
            // 被保护的业务逻辑
            // do something...
            System.out.println("hello world");
        } catch (BlockException e1) {
            // 资源访问阻止，被限流或被降级
            // 进行相应的处理操作
            System.out.println("限流");
            return "限流";
        } finally {
            if (entry != null) {
                entry.exit();
            }
        }
        return "hello world";
    }

    @PostConstruct
    private static void initFlowQpsRule() {
        List<FlowRule> rules = new ArrayList<>();
        FlowRule rule1 = new FlowRule();
        rule1.setResource("hello");
        // Set max qps to 20
        rule1.setCount(1);
        rule1.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rules.add(rule1);
        List<FlowRule> rulesOrigin = FlowRuleManager.getRules();


        FlowRule rule2 = new FlowRule();
        rule2.setResource("anno-hello");
        // Set max qps to 20
        rule2.setCount(1);
        rule2.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rules.add(rule2);
        // 多个规则最好写在一个累的方法中，否则可能失效

        FlowRule rule3 = new FlowRule();
        rule3.setResource("failBlock-test01");
        // Set max qps to 20
        rule3.setCount(1);
        rule3.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rules.add(rule3);

        rulesOrigin.addAll(rules);
        FlowRuleManager.loadRules(rulesOrigin);
    }


}
