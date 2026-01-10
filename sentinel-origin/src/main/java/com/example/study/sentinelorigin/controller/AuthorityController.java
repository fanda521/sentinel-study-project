package com.example.study.sentinelorigin.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.example.study.sentinelorigin.constant.CommonConstant;
import com.example.study.sentinelorigin.handle.AuthorityHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lucksoul
 * @version 1.0
 * @date 2026/1/10 17:49
 */

@RestController
@RequestMapping ("/authority")
public class AuthorityController {


    /**
     * 核心业务接口，受白名单控制
     */
    @SentinelResource(
            value = CommonConstant.WHITE_LIST_RESOURCE,
            blockHandlerClass =  AuthorityHandler.class,
            blockHandler = "whiteListBlockHandler" // 授权规则触发时的降级方法
    )
    @GetMapping("/whiteList")
    public String whiteListCoreBusiness() {
        String format = String.format("【白名单模式-成功】资源：%s，请求允许访问", CommonConstant.WHITE_LIST_RESOURCE);
        System.out.println(format);
        return format;
    }

    /**
     * 核心业务接口，受黑名单控制
     */
    @SentinelResource(
            value = CommonConstant.BLACK_LIST_RESOURCE,
            blockHandlerClass =  AuthorityHandler.class,
            blockHandler = "blackListBlockHandler"
    )
    @GetMapping("/blackList")
    public String blackListCoreBusiness() {
        String format = String.format("【黑名单模式-成功】资源：%s，请求允许访问", CommonConstant.BLACK_LIST_RESOURCE);
        System.out.println(format);
        return format;
    }


}
