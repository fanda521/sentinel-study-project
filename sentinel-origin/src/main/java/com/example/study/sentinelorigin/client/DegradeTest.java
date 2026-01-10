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
 * @author lucksoul
 * @version 1.0
 * @date 2026/1/10 1:21
 */
public class DegradeTest {

    // 目标接口地址（替换为你自己的接口地址，对应 Sentinel 保护的接口）
    private static final String RT_TARGET_URL = "http://localhost:8088/degrade/rt";


    @Test
    public void testRtDegradeResource() {
        // 创建固定线程池
        ExecutorService executorService = Executors.newFixedThreadPool(20);

        // 记录开始时间
        long startTime = System.currentTimeMillis();
        // 提交并发请求任务
        for (int i = 0; i < 2; i++) {
            executorService.submit(() -> {
                for (int j = 0; j < 30; j++) {
                    // 1. 创建 CloseableHttpClient 实例（推荐使用 HttpClients.createDefault()）
                    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                        // 2. 构建 HTTP GET 请求（若接口是 POST，可使用 HttpPost）
                        HttpGet httpGet = new HttpGet(RT_TARGET_URL);
                        // 可选：设置请求头，模拟浏览器请求
                        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
                        httpGet.setHeader("Accept", "application/json, text/plain, */*");

                        // 3. 发送请求，获取响应
                        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                            // 4. 解析响应结果
                            int statusCode = response.getStatusLine().getStatusCode();
                            String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

                            // 5. 打印结果
                            System.out.println("=== 接口响应结果 ===");
                            System.out.println("响应状态码：" + statusCode);
                            System.out.println("响应内容：" + responseBody);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("请求失败：" + e.getMessage());
                    }
                }
            });
        }


        // 关闭线程池，等待所有任务执行完成
        executorService.shutdown();
        while (!executorService.isTerminated()) {}

        // 记录结束时间，打印耗时
        long endTime = System.currentTimeMillis();
        System.out.println("本次请求总耗时：" + (endTime - startTime) + " 毫秒");
    }


    // 目标接口地址（替换为你的接口地址，如 RT 熔断、异常比例熔断接口）
    private static final String EXCEPTION_RATE_TARGET_URL = "http://localhost:8088/degrade/exceptionRate";
    // 并发线程数（可调整，建议 10-20 个，确保触发熔断）
    private static final int EXCEPTION_RATE_THREAD_COUNT = 5;
    // 每个线程发送的请求数（可调整，确保 QPS 超过阈值）
    private static final int EXCEPTION_RATE_REQUEST_PER_THREAD = 20;

    @Test
    public void testExceptionRate() {
        // 1. 创建固定大小线程池
        ExecutorService executorService = Executors.newFixedThreadPool(EXCEPTION_RATE_THREAD_COUNT);
        System.out.println("=== 高并发请求开始，线程数：" + EXCEPTION_RATE_THREAD_COUNT + "，每个线程请求数：" + EXCEPTION_COUNT_REQUEST_PER_THREAD + " ===");

        // 2. 提交线程任务
        for (int i = 0; i < EXCEPTION_RATE_THREAD_COUNT; i++) {
            int threadNum = i + 1;
            executorService.submit(() -> {
                // 每个线程创建一个 HttpClient 实例（线程安全，可复用）
                try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                    for (int j = 0; j < EXCEPTION_RATE_REQUEST_PER_THREAD; j++) {
                        int requestNum = j + 1;
                        try {
                            // 构建 GET 请求
                            HttpGet httpGet = new HttpGet(EXCEPTION_RATE_TARGET_URL);
                            httpGet.setHeader("User-Agent", "HighConcurrentHttpClient/1.0");

                            // 发送请求并获取响应
                            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                                // 解析响应
                                String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                                int statusCode = response.getStatusLine().getStatusCode();

                                // 打印请求结果（可选，可注释以减少输出干扰）
                                System.out.printf("线程 %d - 请求 %d：状态码 %d，响应内容：%s%n",
                                        threadNum, requestNum, statusCode, responseBody);

                                // 轻微休眠，避免请求过于密集导致接口宕机（可选，根据场景调整）
                                TimeUnit.MILLISECONDS.sleep(50);
                            }
                        } catch (Exception e) {
                            System.out.printf("线程 %d - 请求 %d：失败，原因：%s%n",
                                    threadNum, requestNum, e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    System.out.printf("线程 %d：初始化 HttpClient 失败，原因：%s%n", threadNum, e.getMessage());
                }
            });
        }

        // 3. 关闭线程池，等待所有任务完成
        executorService.shutdown();
        try {
            boolean allTaskCompleted = executorService.awaitTermination(5, TimeUnit.MINUTES);
            if (allTaskCompleted) {
                System.out.println("=== 所有高并发请求执行完成 ===");
            } else {
                System.out.println("=== 部分请求超时未完成 ===");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            executorService.shutdownNow();
        }
    }


    // 目标接口地址（替换为你的接口地址，如 RT 熔断、异常比例熔断接口）
    private static final String EXCEPTION_COUNT_TARGET_URL = "http://localhost:8088/degrade/exceptionCount";
    // 并发线程数（可调整，建议 10-20 个，确保触发熔断）
    private static final int EXCEPTION_COUNT_THREAD_COUNT = 5;
    // 每个线程发送的请求数（可调整，确保 QPS 超过阈值）
    private static final int EXCEPTION_COUNT_REQUEST_PER_THREAD = 20;

    @Test
    public void testExceptionCount() {
        // 1. 创建固定大小线程池
        ExecutorService executorService = Executors.newFixedThreadPool(EXCEPTION_COUNT_THREAD_COUNT);
        System.out.println("=== 高并发请求开始，线程数：" + EXCEPTION_COUNT_THREAD_COUNT + "，每个线程请求数：" + EXCEPTION_COUNT_REQUEST_PER_THREAD + " ===");

        // 2. 提交线程任务
        for (int i = 0; i < EXCEPTION_COUNT_THREAD_COUNT; i++) {
            int threadNum = i + 1;
            executorService.submit(() -> {
                // 每个线程创建一个 HttpClient 实例（线程安全，可复用）
                try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                    for (int j = 0; j < EXCEPTION_COUNT_REQUEST_PER_THREAD; j++) {
                        int requestNum = j + 1;
                        try {
                            // 构建 GET 请求
                            HttpGet httpGet = new HttpGet(EXCEPTION_COUNT_TARGET_URL);
                            httpGet.setHeader("User-Agent", "HighConcurrentHttpClient/1.0");

                            // 发送请求并获取响应
                            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                                // 解析响应
                                String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                                int statusCode = response.getStatusLine().getStatusCode();

                                // 打印请求结果（可选，可注释以减少输出干扰）
                                System.out.printf("线程 %d - 请求 %d：状态码 %d，响应内容：%s%n",
                                        threadNum, requestNum, statusCode, responseBody);

                                // 轻微休眠，避免请求过于密集导致接口宕机（可选，根据场景调整）
                                TimeUnit.MILLISECONDS.sleep(50);
                            }
                        } catch (Exception e) {
                            System.out.printf("线程 %d - 请求 %d：失败，原因：%s%n",
                                    threadNum, requestNum, e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    System.out.printf("线程 %d：初始化 HttpClient 失败，原因：%s%n", threadNum, e.getMessage());
                }
            });
        }

        // 3. 关闭线程池，等待所有任务完成
        executorService.shutdown();
        try {
            boolean allTaskCompleted = executorService.awaitTermination(5, TimeUnit.MINUTES);
            if (allTaskCompleted) {
                System.out.println("=== 所有高并发请求执行完成 ===");
            } else {
                System.out.println("=== 部分请求超时未完成 ===");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            executorService.shutdownNow();
        }
    }
}
