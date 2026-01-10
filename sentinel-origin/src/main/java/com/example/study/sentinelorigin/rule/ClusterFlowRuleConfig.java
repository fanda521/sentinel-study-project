package com.example.study.sentinelorigin.rule;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.ClusterFlowConfig;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.example.study.sentinelorigin.controller.ClusterLimitController;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * 集群限流规则配置
 */
@Component
public class ClusterFlowRuleConfig {
    @PostConstruct
    public static void initClusterFlowRule() {
        FlowRule rule = new FlowRule();

        // 1. 绑定受保护的资源名
        rule.setResource(ClusterLimitController.CLUSTER_RESOURCE);
        // 2. 限流指标：QPS
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        // 3. 集群总阈值：10 QPS（整个集群的总流量上限）
        rule.setCount(50);
        // 4. 核心：开启集群限流
        rule.setClusterMode(true);

        // 5. 配置集群限流细节（总体模式）
        ClusterFlowConfig clusterConfig = new ClusterFlowConfig();
        // 1.8.6 版本：strategy=0 即为「集群总阈值控制」（对应高版本 CLUSTER_STRATEGY_TOTAL）
        // 无需配置 setFlowType()，默认即为总体模式，满足集群总流量控制需求
        //clusterConfig.setFlowType(RuleConstant.CLUSTER_FLOW_TYPE_NORMAL); // 总体模式
        clusterConfig.setStrategy(0); // 集群总阈值控制
        clusterConfig.setFallbackToLocalWhenFail(true); // 显式开启容错降级（1.8.6 必配）
        clusterConfig.setClientOfflineTime(3000L); // 可选：配置客户端离线超时，完善配置
        rule.setClusterConfig(clusterConfig);


        // 步骤1：读取现有已加载的规则（转为可修改列表）
        List<FlowRule> existRules = new ArrayList<>(FlowRuleManager.getRules());
        System.out.println("集群规则前的个数：" + existRules.size());
        existRules.add(rule);

        FlowRuleManager.loadRules(existRules);
        System.out.println("===== 集群限流规则加载完成，集群总 QPS 阈值：10 =====");
        System.out.println("集群规则后的个数：" + FlowRuleManager.getRules().size());
    }
}