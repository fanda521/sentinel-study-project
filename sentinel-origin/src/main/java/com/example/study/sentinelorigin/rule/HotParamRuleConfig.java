package com.example.study.sentinelorigin.rule;


import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowItem;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRuleManager;
import com.example.study.sentinelorigin.constant.CommonConstant;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
public class HotParamRuleConfig {
    /**
     * 配置基础热点参数规则：对第 0 个参数限流，QPS 阈值 5
     */
    @PostConstruct
    public void initBasicHotParamRule() {
        List<ParamFlowRule> rules = new ArrayList<>();
        ParamFlowRule rule = new ParamFlowRule();

        // 1. 绑定受保护的资源名
        rule.setResource(CommonConstant.HOT_PARAM_BASIC_RESOURCE);
        // 2. 指定要限流的参数索引（0 = 第 1 个参数 productId）
        rule.setParamIdx(0);
        // 3. 配置 QPS 阈值（每秒最多 5 次请求）
        rule.setCount(5);
        // 4. 限流时长（默认 1 秒，无需修改）
        rule.setDurationInSec(1);

        // ========== 核心：配置参数例外项 ==========
        ParamFlowItem item = new ParamFlowItem();
        item.setObject("999"); // 例外参数值（productId=999）
        item.setCount(100); // 例外项的 QPS 阈值 100
        item.setClassType(String.class.getName()); // 参数类型（必须指定）
        List<ParamFlowItem> paramFlowItemList = new ArrayList<>();
        paramFlowItemList.add(item);
        // 添加例外项到规则
        rule.setParamFlowItemList(paramFlowItemList);

        rules.add(rule);

        /***
         * 多个参数
         */
        // 规则 1：对 orderId（第 0 个参数）限流，QPS 阈值 3
        ParamFlowRule rule1 = new ParamFlowRule();
        rule1.setResource(CommonConstant.HOT_PARAM_MULTI_RESOURCE);
        rule1.setParamIdx(0);
        rule1.setCount(3);

        // 规则 2：对 userId（第 1 个参数）限流，QPS 阈值 5
        ParamFlowRule rule2 = new ParamFlowRule();
        rule2.setResource(CommonConstant.HOT_PARAM_MULTI_RESOURCE);
        rule2.setParamIdx(1);
        rule2.setCount(5);

        rules.add(rule1);
        rules.add(rule2);
        ParamFlowRuleManager.loadRules(rules);
        System.out.println("===== 基础热点参数规则加载完成 =====");
    }
}