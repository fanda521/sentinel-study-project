package com.example.study.sentinelorigin.constant;

/**
 * @author lucksoul
 * @version 1.0
 * @date 2026/1/9 16:51
 */
public class CommonConstant {

    // 定义注解绑定的资源名（也可直接在 @SentinelResource 中写死）
    public static final String THREAD_RESOURCE_NAME = "annotationThreadFlowResource";

    // 资源名
    public static final String LIMIT_APP_RESOURCE = "limitAppResource";

    // 资源名（区分不同流控效果）
    public static final String DEFAULT_RESOURCE = "defaultControlBehaviorResource";
    public static final String WARM_UP_RESOURCE = "warmUpControlBehaviorResource";
    public static final String RATE_LIMITER_RESOURCE = "rateLimiterControlBehaviorResource";

    // 资源名定义
    public static final String DIRECT_RESOURCE = "directStrategyResource"; // 直接流控-当前资源

    public static final String ASSOCIATE_CURRENT_RESOURCE = "associateCurrentResource"; // 关联流控-当前资源（订单创建）
    public static final String ASSOCIATE_REF_RESOURCE = "associateRefResource"; // 关联流控-关联资源（库存扣减）

    public static final String CHAIN_CURRENT_RESOURCE = "chainCurrentResource"; // 链路流控-当前资源（用户查询）
    public static final String CHAIN_ENTRY_RESOURCE = "chainEntryResource"; // 链路流控-入口资源（/api/entry）


}
