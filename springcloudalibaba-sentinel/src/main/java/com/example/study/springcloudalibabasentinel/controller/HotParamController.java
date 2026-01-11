package com.example.study.springcloudalibabasentinel.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lucksoul
 * @version 1.0
 * @date 2026/1/11 17:05
 * 热点参数
 */

@RestController
@RequestMapping("/hotParam")
public class HotParamController {


    @RequestMapping("/single")
    @SentinelResource(value = "/hotParam/single")
    public String single(@RequestParam("id") String id) {
        String result = "【single-成功】-id:" + id;
        System.out.println(result);
        return result;
    }

    @RequestMapping("/singleWithItem")
    @SentinelResource(value = "/hotParam/singleWithItem")
    public String singleWithItem(@RequestParam("id") String id) {
        String result = "【singleWithItem-成功】-id:" + id;
        System.out.println(result);
        return result;
    }

    @RequestMapping("/mutil")
    @SentinelResource(value = "/hotParam/mutil")
    public String mutil(@RequestParam("id") String id, @RequestParam("name") String name) {
        String result = "【mutil-成功】-id:" + id + "name:" + name;
        System.out.println(result);
        return result;
    }

    @RequestMapping("/mutilWithItem")
    @SentinelResource(value = "/hotParam/mutilWithItem")
    public String singleWithItem(@RequestParam("id") String id, @RequestParam("name") String name) {
        String result = "【mutilWithItem-成功】-id:" + id + "name:" + name;
        System.out.println(result);
        return result;
    }
}
