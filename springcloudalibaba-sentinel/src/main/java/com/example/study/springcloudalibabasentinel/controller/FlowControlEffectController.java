package com.example.study.springcloudalibabasentinel.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lucksoul
 * @version 1.0
 * @date 2026/1/11 17:04
 * 流控效果
 */
@RestController
@RequestMapping("/flowControlEffect")
public class FlowControlEffectController {

    @RequestMapping("/failFast")
    public String failFast() {
        String result = "【failFast-成功】";
        System.out.println(result);
        return result;
    }

    @RequestMapping("/warmUp")
    public String warmUp() {
        String result = "【warmUp-成功】";
        System.out.println(result);
        return result;
    }

    @RequestMapping("/queueWait")
    public String queueWait() {
        String result = "【queueWait-成功】";
        System.out.println(result);
        return result;
    }

}
