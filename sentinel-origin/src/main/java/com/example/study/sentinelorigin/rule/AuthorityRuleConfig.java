package com.example.study.sentinelorigin.rule;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRule;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRuleManager;
import com.example.study.sentinelorigin.constant.CommonConstant;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lucksoul
 * @version 1.0
 * @date 2026/1/10 17:52
 */
@Component
public class AuthorityRuleConfig {

    // 白名单来源列表（仅允许这些来源访问）
    private static final String WHITE_LIST_ORIGINS = "internal-app,trusted-client-001";
    // 黑名单来源列表（禁止这些来源访问）
    private static final String BLACK_LIST_ORIGINS = "crawler,malicious-ip-192.168.1.100";

    /**
     * 初始化白名单授权规则
     */
    @PostConstruct
    public void initWhiteListRule() {
        List<AuthorityRule> rules = new ArrayList<>();
        AuthorityRule whiteRule = new AuthorityRule();

        // 1. 绑定受保护的资源
        whiteRule.setResource(CommonConstant.WHITE_LIST_RESOURCE);
        // 2. 指定控制模式：白名单模式
        whiteRule.setStrategy(RuleConstant.AUTHORITY_WHITE);
        // 3. 配置白名单来源（多个来源用 英文逗号 分隔）
        whiteRule.setLimitApp(WHITE_LIST_ORIGINS);

        rules.add(whiteRule);


        AuthorityRule blackRule = new AuthorityRule();

        // 1. 绑定受保护的资源
        blackRule.setResource(CommonConstant.BLACK_LIST_RESOURCE);
        // 2. 指定控制模式：黑名单模式
        blackRule.setStrategy(RuleConstant.AUTHORITY_BLACK);
        // 3. 配置黑名单来源（多个来源用 英文逗号 分隔）
        blackRule.setLimitApp(BLACK_LIST_ORIGINS);

        rules.add(blackRule);

        // 加载授权规则
        AuthorityRuleManager.loadRules(rules);
        System.out.println("===== 白名单授权规则加载完成 =====");
        System.out.println("白名单授权规则个数：" + AuthorityRuleManager.getRules().size());
    }


}
