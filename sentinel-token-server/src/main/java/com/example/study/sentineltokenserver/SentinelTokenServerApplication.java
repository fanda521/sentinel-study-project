package com.example.study.sentineltokenserver;

import com.alibaba.csp.sentinel.cluster.server.ClusterTokenServer;
import com.alibaba.csp.sentinel.cluster.server.SentinelDefaultTokenServer;
import com.alibaba.csp.sentinel.cluster.server.config.ClusterServerConfigManager;
import com.alibaba.csp.sentinel.cluster.server.config.ServerTransportConfig;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.ClusterFlowConfig;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.util.HostNameUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;

@SpringBootApplication
public class SentinelTokenServerApplication {

    // 集群全局QPS阈值（所有客户端总流量上限）
    private static final int CLUSTER_TOTAL_QPS = 50;
    // 客户端兜底QPS阈值（服务端不可用时，客户端本地限流）
    private static final int CLIENT_FALLBACK_QPS = 20;
    // 限流资源名称（与客户端保持一致，大小写敏感）
    private static final String LIMIT_RESOURCE_NAME = "clusterProductResource";

    public static void main(String[] args) throws Exception {
        SpringApplication.run(SentinelTokenServerApplication.class, args);

        // 1. 配置 Token Server 通信端口（客户端连接的端口）
        ServerTransportConfig transportConfig = new ServerTransportConfig()
                .setPort(7070) // 集群通信端口，默认 8719
                .setIdleSeconds(600); // 连接空闲超时时间
        ClusterServerConfigManager.loadGlobalTransportConfig(transportConfig);

        // 步骤2：配置并加载集群流控规则（1.8.6 核心：用 FlowRule 替代 ClusterFlowRule）
        initClusterFlowRuleWithFlowRule();

        // 2. 获取本地 IP（避免使用 localhost，确保客户端能访问）
        String localIp = HostNameUtil.getIp();
        System.out.printf("===== Token Server 启动，IP：%s，端口：%d =====%n", localIp, 7070);

        // 3. 启动 Token Server 实例
        ClusterTokenServer tokenServer = new SentinelDefaultTokenServer();
        tokenServer.start();

        // 4. 保持进程运行
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                tokenServer.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("===== Token Server 已停止 ======");
        }));
    }

    /**
     * 1.8.6 核心适配：用普通 FlowRule 配置集群流控规则（替代不存在的 ClusterFlowRule）
     * 这是 1.8.6 配置集群限流的唯一原生方式，规则会被服务端识别为集群全局规则
     */
    private static void initClusterFlowRuleWithFlowRule() {
        // 构建 1.8.6 原生支持的 FlowRule（普通流控规则类，公开可用，无报错）
        FlowRule clusterFlowRule = new FlowRule();

        // 1. 配置限流资源名称（必须与客户端 SphU.entry() 完全一致）
        clusterFlowRule.setResource(LIMIT_RESOURCE_NAME);

        // 2. 配置限流类型：QPS 限流（1.8.6 原生支持，可选 RuleConstant.FLOW_GRADE_THREAD 线程数限流）
        clusterFlowRule.setGrade(RuleConstant.FLOW_GRADE_QPS);

        // 3. 配置集群全局QPS阈值（核心参数，整个集群所有客户端总流量不超过此值）
        clusterFlowRule.setCount(CLUSTER_TOTAL_QPS);
        // 4. 核心：开启集群限流
        clusterFlowRule.setClusterMode(true);

        // 5. 配置集群限流细节（总体模式）
        ClusterFlowConfig clusterConfig = new ClusterFlowConfig();
        // 1.8.6 版本：strategy=0 即为「集群总阈值控制」（对应高版本 CLUSTER_STRATEGY_TOTAL）
        // 无需配置 setFlowType()，默认即为总体模式，满足集群总流量控制需求
        //clusterConfig.setFlowType(RuleConstant.CLUSTER_FLOW_TYPE_NORMAL); // 总体模式
        clusterConfig.setStrategy(0); // 集群总阈值控制
        clusterConfig.setFallbackToLocalWhenFail(true); // 显式开启容错降级（1.8.6 必配）
        clusterConfig.setClientOfflineTime(3000L); // 可选：配置客户端离线超时，完善配置
        clusterFlowRule.setClusterConfig(clusterConfig);


        // 4. 关键配置：标记为集群规则（1.8.6 隐含支持，服务端会自动识别为全局集群规则）
        // 补充：1.8.6 中，服务端加载的 FlowRule 会默认作为集群全局规则，无需额外标记
        // 配置客户端兜底阈值（服务端不可用时，客户端本地限流阈值，与客户端保持一致）
        System.setProperty("csp.sentinel.cluster.localThreshold." + LIMIT_RESOURCE_NAME, String.valueOf(CLIENT_FALLBACK_QPS));

        // 5. 加载规则（1.8.6 原生方法 FlowRuleManager.loadRules()，无报错，使规则生效）
        FlowRuleManager.loadRules(Collections.singletonList(clusterFlowRule));
    }

}
