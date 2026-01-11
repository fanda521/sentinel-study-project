package com.example.study.springcloudalibabasentinel.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lucksoul
 * @version 1.0
 * @date 2026/1/11 16:45
 * 阀值类型
 */
@RestController
@RequestMapping("/threshold")
public class ThresholdController {

    @RequestMapping("/qps")
    public String qps() {
        String result = "【qps-成功】";
        System.out.println(result);
        return result;
    }

    @RequestMapping("/thread")
    public String thread() {
        String result = "【thread-成功】";
        System.out.println(result);
        return result;
    }
}
