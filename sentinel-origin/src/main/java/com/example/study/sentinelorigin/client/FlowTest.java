package com.example.study.sentinelorigin.client;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FlowTest {
    private static final int THREAD_NUM = 20;
    private static final String TEST_FLOW_THREAD_URL = "http://localhost:8088/flow/annoThread";

    // 测试接口地址
    private static final String TEST_URL = "http://localhost:8088/flow/limitApp";
    // 并发线程数（用于触发限流）
    private static final int CONCURRENT_THREADS = 10;
    @Test
    public void  testFlowThread() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_NUM);
        for (int i = 0; i < THREAD_NUM; i++) {
            executorService.submit(() -> {
                try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                    HttpGet httpGet = new HttpGet(TEST_FLOW_THREAD_URL);
                    String response = EntityUtils.toString(httpClient.execute(httpGet).getEntity());
                    System.out.println(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        Thread.sleep(10000);
        executorService.shutdown();
    }

    @Test
    public void testLimitApp() throws InterruptedException {
        System.out.println("========== 场景 1：测试来源 appA（阈值 2）==========");
        testLimitApp("appA");

        // 间隔 5 秒，让 Sentinel 重置 QPS 统计
        Thread.sleep(5000);

        System.out.println("\n========== 场景 2：测试来源 appB（匹配 other 规则，阈值 5）==========");
        testLimitApp("other");

        // 间隔 5 秒
        Thread.sleep(5000);

        System.out.println("\n========== 场景 3：测试无来源（匹配 other 规则，阈值 5）==========");
        testLimitApp(null);
    }

    /**
     * 模拟指定来源的高并发请求
     * @param appName 应用来源（null 表示无来源）
     */
    private static void testLimitApp(String appName) {
        // 创建固定线程池
        ExecutorService executorService = Executors.newFixedThreadPool(CONCURRENT_THREADS);

        // 提交并发请求任务
        for (int i = 0; i < CONCURRENT_THREADS; i++) {
            executorService.submit(() -> {
                try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                    // 构建 GET 请求
                    HttpGet httpGet = new HttpGet(TEST_URL);

                    // 若指定了应用来源，添加请求头 X-Sentinel-App
                    if (appName != null && !appName.isEmpty()) {
                        httpGet.addHeader("X-Sentinel-App", appName);
                    }

                    // 执行请求并获取响应结果
                    String response = EntityUtils.toString(httpClient.execute(httpGet).getEntity(), "UTF-8");
                    System.out.println(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        // 关闭线程池，等待所有任务执行完成
        executorService.shutdown();
        while (!executorService.isTerminated()) {
            // 等待任务结束
        }
    }


}
