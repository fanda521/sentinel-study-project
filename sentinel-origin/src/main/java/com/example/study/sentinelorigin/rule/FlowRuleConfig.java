package com.example.study.sentinelorigin.rule;

import com.alibaba.csp.sentinel.adapter.servlet.callback.WebCallbackManager;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.example.study.sentinelorigin.config.CustomRequestOriginParser;
import com.example.study.sentinelorigin.constant.CommonConstant;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private CustomRequestOriginParser customRequestOriginParser;
    @PostConstruct
    public void registerRequestOriginParser() {
        // 关键：将 Spring 注入的 Bean 注册到 Sentinel 的回调管理器中
        WebCallbackManager.setRequestOriginParser(customRequestOriginParser);
        System.out.println("========== CustomRequestOriginParser 已注册到 Sentinel ==========");
    }

    @PostConstruct
    public void initAnnotationThreadFlowRules() {
        FlowRule rule = new FlowRule();
        rule.setResource(CommonConstant.THREAD_RESOURCE_NAME); // 绑定注解对应的资源名
        rule.setGrade(RuleConstant.FLOW_GRADE_THREAD); // 限流类型：并发线程数（核心）
        rule.setCount(5); // 最大并发线程数阈值：5
        rule.setLimitApp("default"); // 针对默认应用限流

        // 步骤1：读取现有已加载的规则（转为可修改列表）
        List<FlowRule> existingRules = new ArrayList<>(FlowRuleManager.getRules());
        System.out.println("追加前，现有规则数：" + existingRules.size());
        // 步骤3：调用loadRules()重新加载（实现追加效果）
        existingRules.add(rule);



        // limitApp
        // 规则 1：仅对应用 "appA" 进行 QPS 限流（阈值 2）
        FlowRule appARule = new FlowRule();
        appARule.setResource(CommonConstant.LIMIT_APP_RESOURCE); // 绑定资源
        appARule.setGrade(RuleConstant.FLOW_GRADE_THREAD); // QPS 限流
        appARule.setCount(2); // 每秒最多 2 个请求
        appARule.setLimitApp("appA"); // 仅对 appA 生效（核心配置）

        // 规则 2：对除 appA 之外的所有其他来源（兜底）进行 QPS 限流（阈值 5）
        FlowRule otherRule = new FlowRule();
        otherRule.setResource(CommonConstant.LIMIT_APP_RESOURCE);
        otherRule.setGrade(RuleConstant.FLOW_GRADE_THREAD);
        otherRule.setCount(5);
        otherRule.setLimitApp("other"); // 对非 appA 来源生效（核心配置）
        existingRules.add(appARule);
        existingRules.add(otherRule);


        FlowRuleManager.loadRules(existingRules);
        System.out.println("追加后，当前生效规则数：" + FlowRuleManager.getRules().size());
    }
}
