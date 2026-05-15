package com.rbac.system.auth;

import com.rbac.system.auth.dto.LoginRequest;
import com.rbac.system.auth.dto.LoginResponse;
import com.rbac.system.auth.dto.UserInfoResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    void logout(String token);
    UserInfoResponse getUserInfo(Long userId);
}