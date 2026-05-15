package com.rbac.framework.captcha;

import com.rbac.common.constant.SystemConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class CaptchaService {
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${captcha.enabled:true}")
    private boolean captchaEnabled;

    public String generateCaptcha(String uuid) {
        if (!captchaEnabled) {
            return "";
        }
        String code = String.format("%04d", new Random().nextInt(10000));
        String key = SystemConstants.CAPTCHA_PREFIX + uuid;
        redisTemplate.opsForValue().set(key, code, SystemConstants.CAPTCHA_EXPIRE_SECONDS, TimeUnit.SECONDS);
        return code;
    }

    public boolean validateCaptcha(String uuid, String code) {
        if (!captchaEnabled) {
            return true;
        }
        String key = SystemConstants.CAPTCHA_PREFIX + uuid;
        Object stored = redisTemplate.opsForValue().get(key);
        if (stored == null) {
            return false;
        }
        redisTemplate.delete(key);
        return stored.toString().equalsIgnoreCase(code);
    }
}