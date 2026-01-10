package com.example.study.sentinelorigin.client;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 集群限流高并发测试客户端
 */
public class ClusterLimitTestClient {
    // 两个客户端实例地址
    private static final String[] CLIENT_URLS = {
            "http://localhost:8088/cluster/product/query?productId=1001",
            "http://localhost:8089/cluster/product/query?productId=1001"
    };
    private static final int THREAD_COUNT = 20;
    private static final int REQUEST_PER_THREAD = 5;

    @Test
    public void testClusterLimit() {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        for (int i = 0; i < THREAD_COUNT; i++) {
            int index = i % 2; // 轮流访问两个客户端实例
            executor.submit(() -> {
                try (CloseableHttpClient client = HttpClients.createDefault()) {
                    for (int j = 0; j < REQUEST_PER_THREAD; j++) {
                        HttpGet get = new HttpGet(CLIENT_URLS[index]);
                        try (CloseableHttpResponse response = client.execute(get)) {
                            String result = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                            System.out.println(result);
                            TimeUnit.MILLISECONDS.sleep(50);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        executor.shutdown();
        while (!executor.isTerminated()) {}
    }
}