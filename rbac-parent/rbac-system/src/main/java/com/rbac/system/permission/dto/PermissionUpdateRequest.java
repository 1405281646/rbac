package com.rbac.system.permission.dto;

import lombok.Data;

@Data
public class PermissionUpdateRequest {
    private String name;
    private Integer type;
    private Long parentId;
    private String path;
    private String component;
    private String perms;
    private String icon;
    private Integer sort;
    private Integer visible;
}