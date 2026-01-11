package com.example.study.springcloudalibabasentinel.config;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.lang.reflect.UndeclaredThrowableException;

@ControllerAdvice // 确保该类被 Spring 扫描到（添加在启动类同包或子包下）
public class GlobalSentinelExceptionHandler {

    // 注入 Spring 内置的 ObjectMapper（推荐，避免重复创建）
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 第一步：捕获 AOP 封装的 UndeclaredThrowableException
     * 解包获取底层的 BlockException
     */
    @ExceptionHandler(UndeclaredThrowableException.class)
    public ResponseEntity<ResultVo> handleUndeclaredThrowableException(UndeclaredThrowableException e) throws JsonProcessingException {
        // 提取底层真正的异常
        Throwable rootCause = e.getUndeclaredThrowable();
        // 判断是否为 Sentinel 的 BlockException
        if (rootCause instanceof BlockException) {
            return handleSentinelBlockException((BlockException) rootCause);
        }
        // 非 BlockException，返回通用错误
        ResultVo resultVo = new ResultVo();
        resultVo.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        resultVo.setMsg("服务器内部错误：" + rootCause.getMessage());
        System.out.println("服务器内部错误[UndeclaredThrowableException]：" + objectMapper.writeValueAsString(resultVo));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resultVo);
    }

    /**
     * 第二步：捕获 Sentinel 原生 BlockException（兼容直接抛出的场景）
     */
    @ExceptionHandler(BlockException.class)
    public ResponseEntity<ResultVo> handleSentinelBlockException(BlockException e) throws JsonProcessingException {
        ResultVo resultVo = new ResultVo();
        resultVo.setCode(HttpStatus.TOO_MANY_REQUESTS.value()); // 429 状态码

        // 区分不同 Sentinel 异常类型，返回精准提示
        if (e instanceof FlowException) {
            resultVo.setMsg("接口流控限制，请求过于频繁");
        } else if (e instanceof ParamFlowException) {
            resultVo.setMsg("热点参数限流限制，请求过于频繁");
        } else {
            resultVo.setMsg("Sentinel 系统保护拦截：" + e.getClass().getSimpleName());
        }

        System.out.println("服务器内部错误[UndeclaredThrowableException]：" + objectMapper.writeValueAsString(resultVo));
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(resultVo);
    }

    /**
     * 自定义响应体（完善 getter/setter，确保 JSON 序列化正常）
     */
    static class ResultVo {
        private int code;
        private String msg;
        private Object data;

        // 必须添加 getter/setter，否则返回的 JSON 无字段
        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }
    }
}