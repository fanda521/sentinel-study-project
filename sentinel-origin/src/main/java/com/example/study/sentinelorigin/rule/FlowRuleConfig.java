package com.example.study.sentinelorigin.rule;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.example.study.sentinelorigin.constant.CommonConstant;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lucksoul
 * @version 1.0
 * @date 2026/1/9 16:49
 */
@Component
public class FlowRuleConfig {

    @PostConstruct
    public void initAnnotationThreadFlowRules() {
        FlowRule rule = new com.alibaba.csp.sentinel.slots.block.flow.FlowRule();
        rule.setResource(CommonConstant.THREAD_RESOURCE_NAME); // 绑定注解对应的资源名
        rule.setGrade(RuleConstant.FLOW_GRADE_THREAD); // 限流类型：并发线程数（核心）
        rule.setCount(5); // 最大并发线程数阈值：5
        rule.setLimitApp("default"); // 针对默认应用限流

        // 步骤1：读取现有已加载的规则（转为可修改列表）
        List<FlowRule> existingRules = new ArrayList<>(FlowRuleManager.getRules());
        System.out.println("追加前，现有规则数：" + existingRules.size());
        // 步骤3：调用loadRules()重新加载（实现追加效果）
        existingRules.add(rule);
        FlowRuleManager.loadRules(existingRules);
        System.out.println("追加后，当前生效规则数：" + FlowRuleManager.getRules().size());
    }
}
