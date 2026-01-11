package com.example.study.springcloudalibabasentinel.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author lucksoul
 * @version 1.0
 * @date 2026/1/11 17:07
 * 来源访问控制
 */
@RestController
@RequestMapping("/authority")
public class AuthorityController {

    @RequestMapping("/whiteList")
    public String whiteList(HttpServletRequest request) {
        String result = "【whiteList-成功】,X-Sentinel-App:" + request.getHeader("X-Sentinel-App").toString();
        System.out.println(result);
        return result;
    }

    @RequestMapping("/blackList")
    public String blackList(HttpServletRequest  request) {
        String result = "【blackList-成功】,X-Sentinel-App:" + request.getHeader("X-Sentinel-App").toString();
        System.out.println(result);
        return result;
    }
}
