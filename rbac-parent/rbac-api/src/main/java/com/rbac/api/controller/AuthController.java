package com.rbac.api.controller;

import com.rbac.common.constant.SystemConstants;
import com.rbac.common.response.Result;
import com.rbac.framework.captcha.CaptchaService;
import com.rbac.framework.security.UserContext;
import com.rbac.system.auth.AuthService;
import com.rbac.system.auth.dto.LoginRequest;
import com.rbac.system.auth.dto.LoginResponse;
import com.rbac.system.auth.dto.UserInfoResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final CaptchaService captchaService;

    @GetMapping("/captcha")
    public Result<Map<String, String>> captcha() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String code = captchaService.generateCaptcha(uuid);
        Map<String, String> result = new HashMap<>();
        result.put("uuid", uuid);
        result.put("captchaEnabled", String.valueOf(!code.isEmpty()));
        return Result.success(result);
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(authService.login(request));
    }

    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        String authHeader = request.getHeader(SystemConstants.TOKEN_HEADER);
        String token = null;
        if (authHeader != null && authHeader.startsWith(SystemConstants.TOKEN_PREFIX)) {
            token = authHeader.substring(SystemConstants.TOKEN_PREFIX.length());
        }
        authService.logout(token);
        return Result.success();
    }

    @GetMapping("/info")
    public Result<UserInfoResponse> info() {
        Long userId = UserContext.getUserId();
        return Result.success(authService.getUserInfo(userId));
    }
}