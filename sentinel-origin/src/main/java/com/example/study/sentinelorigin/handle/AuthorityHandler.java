package com.example.study.sentinelorigin.handle;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.example.study.sentinelorigin.constant.CommonConstant;

/**
 * @author lucksoul
 * @version 1.0
 * @date 2026/1/10 17:51
 */
public class AuthorityHandler {


    /**
     * 白名单拦截降级方法（必须与原方法参数一致，末尾追加 BlockException）
     */
    public static String whiteListBlockHandler(BlockException e) {
        String format = String.format("【白名单模式-拒绝】资源：%s，来源不在白名单中，禁止访问", CommonConstant.WHITE_LIST_RESOURCE);
        System.out.println(format);
        return format;
    }



    /**
     * 黑名单拦截降级方法
     */
    public static String blackListBlockHandler(BlockException e) {
        String format = String.format("【黑名单模式-拒绝】资源：%s，来源在黑名单中，禁止访问", CommonConstant.BLACK_LIST_RESOURCE);
        System.out.println(format);
        return format;
    }

}
