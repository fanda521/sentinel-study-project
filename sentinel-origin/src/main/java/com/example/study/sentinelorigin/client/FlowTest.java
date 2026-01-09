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


}
