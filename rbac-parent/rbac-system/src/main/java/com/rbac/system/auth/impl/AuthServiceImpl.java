package com.rbac.system.auth.impl;

import com.rbac.common.constant.SystemConstants;
import com.rbac.common.exception.BadRequestException;
import com.rbac.framework.captcha.CaptchaService;
import com.rbac.framework.security.JwtUtils;
import com.rbac.system.auth.AuthService;
import com.rbac.system.auth.dto.LoginRequest;
import com.rbac.system.auth.dto.LoginResponse;
import com.rbac.system.auth.dto.UserInfoResponse;
import com.rbac.system.user.entity.SysUser;
import com.rbac.system.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final CaptchaService captchaService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public LoginResponse login(LoginRequest request) {
        if (!captchaService.validateCaptcha(request.getUuid(), request.getCaptchaCode())) {
            throw new BadRequestException("验证码错误或已过期");
        }
        SysUser user = userService.getByUsername(request.getUsername());
        if (user == null) {
            throw new BadRequestException("用户名或密码错误");
        }
        if (user.getStatus() == 0) {
            throw new BadRequestException("账号已被禁用");
        }
        String token = jwtUtils.generateToken(user.getId(), user.getUsername());
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
                user.getId(), user.getUsername(), user.getRealName(), user.getAvatar());
        return new LoginResponse(token, userInfo);
    }

    @Override
    public void logout(String token) {
        if (StringUtils.hasText(token)) {
            String blacklistKey = SystemConstants.REDIS_BLACKLIST_PREFIX + token;
            redisTemplate.opsForValue().set(blacklistKey, "1", SystemConstants.TOKEN_EXPIRE_SECONDS, TimeUnit.SECONDS);
        }
    }

    @Override
    public UserInfoResponse getUserInfo(Long userId) {
        SysUser user = userService.getById(userId);
        if (user == null) {
            throw new BadRequestException("用户不存在");
        }
        UserInfoResponse resp = new UserInfoResponse();
        resp.setId(user.getId());
        resp.setUsername(user.getUsername());
        resp.setRealName(user.getRealName());
        resp.setAvatar(user.getAvatar());
        return resp;
    }
}