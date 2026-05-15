package com.rbac.common.constant;

public interface SystemConstants {
    String TOKEN_HEADER = "Authorization";
    String TOKEN_PREFIX = "Bearer ";
    String REDIS_BLACKLIST_PREFIX = "blacklist:";
    String CAPTCHA_PREFIX = "captcha:";
    long CAPTCHA_EXPIRE_SECONDS = 300;
    long TOKEN_EXPIRE_SECONDS = 86400;
    String ADMIN_ROLE_CODE = "admin";
}