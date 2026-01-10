package com.example.study.sentinelorigin.config;

import com.example.study.sentinelorigin.service.PureLocalChainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

//@Component
public class PureLocalTestRunner implements CommandLineRunner {

    @Autowired
    private PureLocalChainService chainService;

    @Override
    public void run(String... args) throws Exception {
        // 10 个线程，每个线程循环 10 次，总请求 100 次，QPS 远超 2
        ExecutorService executor = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            executor.submit(() -> {
                for (int j = 0; j < 10; j++) {
                    String result = chainService.entryMethod();
                    System.out.println(result);
                    // 轻微休眠，避免请求太密集导致打印混乱
                    try {
                        TimeUnit.MILLISECONDS.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
    }
}