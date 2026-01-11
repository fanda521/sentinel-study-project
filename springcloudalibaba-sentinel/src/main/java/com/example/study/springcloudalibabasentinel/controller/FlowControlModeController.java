package com.example.study.springcloudalibabasentinel.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.example.study.springcloudalibabasentinel.service.FlowControlModeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lucksoul
 * @version 1.0
 * @date 2026/1/11 17:00
 * 流控模式
 */
@RestController
@RequestMapping("/flowControlMode")
public class FlowControlModeController {

    @Autowired
    private FlowControlModeService flowControlModeService;


    @RequestMapping("/direct")
    public String direct() {
        String result = "【direct-成功】";
        System.out.println(result);
        return result;
    }

    @RequestMapping("/association")
    public String association() {
        String result = "【association-成功】";
        System.out.println(result);
        return result;
    }

    @RequestMapping("/associationRef")
    public String associationRef() {
        String result = "【associationRef-成功】";
        System.out.println(result);
        return result;
    }

    @RequestMapping("/link")
    public String link() {
        String result = flowControlModeService.link();
        System.out.println(result);
        return result;
    }

    @RequestMapping("/linkEnter")
    public String linkEnter() {
        String s = "linkEnter-" + flowControlModeService.link();
        System.out.println(s);
        return s;
    }

    /**
     * 测试链路-controller项目调用
     * 结果都失败了，只能像上面那样，controller 调用 service
     * @return
     */
    @RequestMapping("/linkController")
    @SentinelResource(value = "flowControlMode/linkController")
    public String linkController() {
        String result = "linkController";
        return result;
    }

    @RequestMapping("/linkEnterController")
    public String linkEnterController() {
        String s = "linkEnter-" + linkController();
        System.out.println(s);
        return s;
    }

}
