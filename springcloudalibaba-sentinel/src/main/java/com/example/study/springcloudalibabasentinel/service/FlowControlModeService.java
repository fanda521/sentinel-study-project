package com.example.study.springcloudalibabasentinel.service;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.springframework.stereotype.Service;

/**
 * @author lucksoul
 * @version 1.0
 * @date 2026/1/11 21:25
 */
@Service
public class FlowControlModeService {

    @SentinelResource(value = "serviceLink",blockHandler = "serviceLinkBlockException")
    public String link() {
        String result = "【service-link-成功】";
        return result;
    }

    public String serviceLinkBlockException(BlockException e) {
        String result = "【service-link-降级】";
        return result;
    }
}
