package com.example.study.sentinelorigin.service;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.example.study.sentinelorigin.constant.CommonConstant;
import com.example.study.sentinelorigin.handle.FlowHandler;
import org.springframework.stereotype.Service;
import com.alibaba.csp.sentinel.annotation.SentinelResource;

import java.util.concurrent.TimeUnit;

@Service
public class ChainFlowCoreService {

    /**
     * 核心：Service 层标记当前资源，作为链路流控的保护目标
     * 降级方法直接内联（避免外部类静态方法的额外问题，先确保链路生效）
     */
    @SentinelResource(
            value = CommonConstant.CHAIN_CURRENT_RESOURCE,
            blockHandlerClass = FlowHandler.class,
            blockHandler = "chainCurrentBlockHandler" // 内联降级方法，无需 static
    )
    public String doCoreUserQuery() throws InterruptedException {
        // 业务逻辑：模拟用户查询，打印成功日志
        String successLog = String.format("【链路流控-当前资源成功】线程：%s，请求正常执行", 
                Thread.currentThread().getName());
        System.out.println(successLog);
        return successLog;
    }

}