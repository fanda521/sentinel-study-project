package com.example.study.sentinelorigin.rule;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.example.study.sentinelorigin.constant.CommonConstant;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lucksoul
 * @version 1.0
 * @date 2026/1/10 0:52
 */

@Component
public class DegradeRuleConfig {

    // ========== 配置 RT 熔断规则 ==========
    @PostConstruct
    public void initRtDegradeRule() {
        List<DegradeRule> rules = new ArrayList<>();
        DegradeRule rule = new DegradeRule();

        // 1. 绑定保护资源
        rule.setResource(CommonConstant.RT_DEGRADE_RESOURCE);
        // 2. 指定熔断方式：基于平均响应时间（RT）
        rule.setGrade(RuleConstant.DEGRADE_GRADE_RT);
        // 3. 配置 RT 阈值：100 毫秒（超过该值则计入慢请求）
        rule.setCount(200);
        // 4. 配置熔断窗口时间：5 秒（窗口内所有请求直接被拒绝）
        rule.setTimeWindow(5);
        // 5. 配置最小请求数：5（1 秒内请求数超过 5 才会触发熔断，默认 5）
        rule.setMinRequestAmount(5);
        // 6. 配置慢请求比例阈值：0.5（可选，慢请求占比超过 50% 才触发，默认 0.5）
        rule.setSlowRatioThreshold(0.5);

        rules.add(rule);

        DegradeRuleManager.loadRules(rules);
        System.out.println("===== 基于 RT 的熔断规则加载完成 =====");


        List<DegradeRule> degradeRules = DegradeRuleManager.getRules();
        System.out.println("before add size=" + degradeRules.size());


        DegradeRule rule2 = new DegradeRule();
        // 1. 绑定保护资源
        rule2.setResource(CommonConstant.RATIO_DEGRADE_RESOURCE);
        // 2. 指定熔断方式：基于异常比例
        rule2.setGrade(RuleConstant.DEGRADE_GRADE_EXCEPTION_RATIO);
        // 3. 配置异常比例阈值：0.5（50%，超过该比例则触发熔断）
        rule2.setCount(0.5);
        // 4. 配置熔断窗口时间：5 秒
        rule2.setTimeWindow(5);
        // 5. 配置最小请求数：5（1 秒内请求数超过 5 才会触发熔断）
        rule2.setMinRequestAmount(5);
        degradeRules.add(rule2);


        DegradeRule rule3 = new DegradeRule();

        // 1. 绑定保护资源
        rule3.setResource(CommonConstant.COUNT_DEGRADE_RESOURCE);
        // 2. 指定熔断方式：基于异常数
        rule3.setGrade(RuleConstant.DEGRADE_GRADE_EXCEPTION_COUNT);
        // 3. 配置异常数阈值：3（统计窗口内异常数超过 3 则触发熔断）
        rule3.setCount(3);
        // 4. 配置熔断窗口时间：5 秒
        rule3.setTimeWindow(5);
        // 5. 配置最小请求数：1（低 QPS 场景，降低最小请求数要求）
        rule3.setMinRequestAmount(1);
        degradeRules.add(rule3);

        DegradeRuleManager.loadRules(degradeRules);
        System.out.println("after add size=" + DegradeRuleManager.getRules().size());
    }
}
