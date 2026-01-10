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
 * @date 2026/1/10 18:35
 */
public class HotParamTest {

    private static final String TARGET_URL = "http://localhost:8088/hotParam/product/query?productId=1001";
    private static final String PARAM_ITEM_TARGET_URL = "http://localhost:8088/hotParam/product/query?productId=999";
    private static final String PARAM_MUTIL_TARGET_URL = "http://localhost:8088/hotParam/order/query?orderId=111&userId=luck";
    private static final int THREAD_COUNT = 10;
    private static final int REQUEST_PER_THREAD = 2;

    @Test
    public void testHotParam() {
        testHotParamWithThread(TARGET_URL, THREAD_COUNT, REQUEST_PER_THREAD);
    }

    @Test
    public void testHotParamWithParamItem() {
        testHotParamWithThread(PARAM_ITEM_TARGET_URL,15,6);
    }

    @Test
    public void testHotParamWithParamMutil() {
        testHotParamWithThread(PARAM_MUTIL_TARGET_URL,15,6);
    }

    public void testHotParamWithThread(String url,int threadCount, int requestPerThread) {
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try (CloseableHttpClient client = HttpClients.createDefault()) {
                    for (int j = 0; j < requestPerThread; j++) {
                        HttpGet get = new HttpGet(url);
                        try (CloseableHttpResponse response = client.execute(get)) {
                            String result = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                            System.out.println(result);
                            TimeUnit.MILLISECONDS.sleep(100);
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
