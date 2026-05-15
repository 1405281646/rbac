package com.rbac.system.user.dto;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String realName;
    private String email;
    private String phone;
    private String avatar;
    private Integer status;
    private Long deptId;
}