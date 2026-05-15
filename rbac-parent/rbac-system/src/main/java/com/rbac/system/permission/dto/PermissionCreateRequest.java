package com.rbac.system.permission.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PermissionCreateRequest {
    @NotBlank(message = "权限名称不能为空")
    private String name;

    @NotNull(message = "权限类型不能为空")
    private Integer type;

    private Long parentId;
    private String path;
    private String component;
    private String perms;
    private String icon;
    private Integer sort;
    private Integer visible;
}