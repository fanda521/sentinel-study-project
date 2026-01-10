package com.example.study.sentinelorigin.config;

import com.alibaba.csp.sentinel.cluster.ClusterTransportClient;
import com.alibaba.csp.sentinel.cluster.client.DefaultClusterTokenClient;
import com.alibaba.csp.sentinel.cluster.client.NettyTransportClient;
import com.alibaba.csp.sentinel.cluster.client.config.ClusterClientAssignConfig;
import com.alibaba.csp.sentinel.cluster.client.config.ClusterClientConfig;
import com.alibaba.csp.sentinel.cluster.client.config.ClusterClientConfigManager;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * Sentinel 集群限流客户端配置
 */
@Configuration
public class SentinelClusterClientConfig {
    // Token Server 的 IP 和端口
    private static final String TOKEN_SERVER_IP = "127.0.0.1";
    private static final int TOKEN_SERVER_PORT = 7070;

    // 可选：连接超时/读取超时时间（毫秒）
    private static final int CONNECT_TIMEOUT = 3000;
    private static final int READ_TIMEOUT = 3000;

    /**
     * 注册 Sentinel 注解切面（必须，确保 @SentinelResource 生效）
     */


    /**
     * 初始化集群客户端 + 集群限流规则（简化版，绕过隐藏类）
     */
    @PostConstruct
    public void initClusterClientAndRule() {
        try {
            ClusterTransportClient transportClient = new NettyTransportClient(TOKEN_SERVER_IP, TOKEN_SERVER_PORT);
            transportClient.start();
            System.out.println("===== Sentinel 1.8.6 集群客户端初始化完成（连接 Token Server："
                    + TOKEN_SERVER_IP + ":" + TOKEN_SERVER_PORT + "） =====");
        } catch (Exception e) {
            System.err.println("===== Sentinel 1.8.6 集群客户端初始化失败 =====");
            e.printStackTrace();
        }
    }

}