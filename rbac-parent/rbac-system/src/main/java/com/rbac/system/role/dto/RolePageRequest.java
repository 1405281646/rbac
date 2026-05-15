package com.rbac.system.role.dto;

import lombok.Data;

@Data
public class RolePageRequest {
    private int page = 1;
    private int size = 10;
    private String keyword;
    private Integer status;
}