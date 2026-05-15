package com.rbac.system.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserCreateRequest {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    private String realName;
    private String email;
    private String phone;
    private String avatar;
    private Integer status;
    private Long deptId;
}