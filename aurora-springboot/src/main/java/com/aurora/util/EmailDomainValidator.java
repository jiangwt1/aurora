package com.aurora.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 邮箱域名验证工具类
 */
public class EmailDomainValidator {
    
    // 允许的邮箱域名白名单
    private static final Set<String> ALLOWED_DOMAINS = new HashSet<>(Arrays.asList(
        "gmail.com",
        "qq.com",
        "163.com",
        "126.com",
        "sina.com",
        "sohu.com",
        "hotmail.com",
        "outlook.com",
        "yahoo.com",
        "foxmail.com",
        "aliyun.com",
        "139.com",
        "wo.com.cn"
    ));
    
    // 被禁止的临时邮箱域名黑名单
    private static final Set<String> BLOCKED_DOMAINS = new HashSet<>(Arrays.asList(
        "10minutemail.com",
        "guerrillamail.com",
        "mailinator.com",
        "tempmail.org",
        "yopmail.com",
        "throwaway.email",
        "temp-mail.org",
        "maildrop.cc",
        "sharklasers.com",
        "guerrillamailblock.com",
        "pokemail.net",
        "spam4.me",
        "bccto.me",
        "chacuo.net",
        "027168.com"
    ));
    
    /**
     * 验证邮箱域名是否被允许
     * @param email 邮箱地址
     * @return true表示允许，false表示不允许
     */
    public static boolean isAllowedDomain(String email) {
        if (email == null || !email.contains("@")) {
            return false;
        }
        
        String domain = email.substring(email.lastIndexOf("@") + 1).toLowerCase();
        
        // 检查是否在黑名单中
        if (BLOCKED_DOMAINS.contains(domain)) {
            return false;
        }
        
        // 检查是否在白名单中
        return ALLOWED_DOMAINS.contains(domain);
    }
    
    /**
     * 获取邮箱域名
     * @param email 邮箱地址
     * @return 域名
     */
    public static String getDomain(String email) {
        if (email == null || !email.contains("@")) {
            return null;
        }
        return email.substring(email.lastIndexOf("@") + 1).toLowerCase();
    }
    
    /**
     * 检查是否为临时邮箱
     * @param email 邮箱地址
     * @return true表示是临时邮箱
     */
    public static boolean isTemporaryEmail(String email) {
        if (email == null || !email.contains("@")) {
            return false;
        }
        
        String domain = getDomain(email);
        return BLOCKED_DOMAINS.contains(domain);
    }
}
