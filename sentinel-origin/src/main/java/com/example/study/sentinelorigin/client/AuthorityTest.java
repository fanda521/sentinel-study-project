package com.example.study.sentinelorigin.client;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

/**
 * @author lucksoul
 * @version 1.0
 * @date 2026/1/10 17:55
 */
public class AuthorityTest {

    private static final String TARGET_URL = "http://localhost:8088/authority/whiteList";

    @Test
    public void testAuthorityWhiteList() {
        // 测试 1：发送白名单内的来源（internal-app）
        sendRequestWithOrigin("internal-app");
        // 测试 2：发送白名单内的来源（trusted-client-001）
        sendRequestWithOrigin("trusted-client-001");
        // 测试 3：发送白名单外的来源（untrusted-client）
        sendRequestWithOrigin("untrusted-client");
    }

    /**
     * 携带来源标识发送 HTTP 请求
     * @param origin 来源标识
     */
    private static void sendRequestWithOrigin(String origin) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(TARGET_URL);
            // 添加自定义请求头，传递来源标识
            httpGet.setHeader("X-Sentinel-App", origin);

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                System.out.printf("来源：%s，响应结果：%s%n", origin, responseBody);
            }
        } catch (Exception e) {
            System.out.printf("来源：%s，请求失败：%s%n", origin, e.getMessage());
        }
    }

    private static final String BLACK_TARGET_URL = "http://localhost:8088/authority/blackList";

    @Test
    public void testAuthorityBlackList() {
        // 测试 1：发送黑名单内的来源（crawler）
        sendRequestWithOriginBlack("crawler");
        // 测试 2：发送黑名单内的来源（malicious-ip-192.168.1.100）
        sendRequestWithOriginBlack("malicious-ip-192.168.1.100");
        // 测试 3：发送黑名单外的来源（normal-client-002）
        sendRequestWithOriginBlack("normal-client-002");
    }

    private static void sendRequestWithOriginBlack(String origin) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(BLACK_TARGET_URL);
            httpGet.setHeader("X-Sentinel-App", origin);

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                System.out.printf("来源：%s，响应结果：%s%n", origin, responseBody);
            }
        } catch (Exception e) {
            System.out.printf("来源：%s，请求失败：%s%n", origin, e.getMessage());
        }
    }


}
