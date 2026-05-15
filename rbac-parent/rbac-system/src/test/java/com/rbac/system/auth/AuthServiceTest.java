package com.rbac.system.auth;

import com.rbac.common.exception.BadRequestException;
import com.rbac.framework.captcha.CaptchaService;
import com.rbac.framework.security.JwtUtils;
import com.rbac.system.auth.dto.LoginRequest;
import com.rbac.system.auth.dto.LoginResponse;
import com.rbac.system.auth.impl.AuthServiceImpl;
import com.rbac.system.user.entity.SysUser;
import com.rbac.system.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock private UserService userService;
    @Mock private JwtUtils jwtUtils;
    @Mock private CaptchaService captchaService;
    @Mock private RedisTemplate<String, Object> redisTemplate;
    @Mock private ValueOperations<String, Object> valueOps;

    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(userService, jwtUtils, captchaService, redisTemplate);
    }

    @Test
    void login_withValidCredentials_shouldReturnToken() {
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("admin123");
        request.setUuid("test-uuid");
        request.setCaptchaCode("1234");

        SysUser user = new SysUser();
        user.setId(1L);
        user.setUsername("admin");
        user.setPassword("encoded-password");
        user.setRealName("管理员");
        user.setStatus(1);

        when(captchaService.validateCaptcha("test-uuid", "1234")).thenReturn(true);
        when(userService.getByUsername("admin")).thenReturn(user);
        when(jwtUtils.generateToken(1L, "admin")).thenReturn("test-token");

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("test-token", response.getToken());
        assertEquals("admin", response.getUser().getUsername());
    }

    @Test
    void login_withWrongCaptcha_shouldThrowException() {
        LoginRequest request = new LoginRequest();
        request.setUuid("test-uuid");
        request.setCaptchaCode("wrong");

        when(captchaService.validateCaptcha("test-uuid", "wrong")).thenReturn(false);

        assertThrows(BadRequestException.class, () -> authService.login(request));
    }

    @Test
    void login_withDisabledUser_shouldThrowException() {
        LoginRequest request = new LoginRequest();
        request.setUsername("disabled");
        request.setPassword("pwd");
        request.setUuid("uuid");
        request.setCaptchaCode("1234");

        SysUser user = new SysUser();
        user.setStatus(0);

        when(captchaService.validateCaptcha("uuid", "1234")).thenReturn(true);
        when(userService.getByUsername("disabled")).thenReturn(user);

        assertThrows(BadRequestException.class, () -> authService.login(request));
    }

    @Test
    void logout_shouldAddTokenToBlacklist() {
        when(redisTemplate.opsForValue()).thenReturn(valueOps);

        authService.logout("test-token");

        verify(valueOps).set(anyString(), anyString(), anyLong(), any());
    }
}