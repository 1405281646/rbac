package com.rbac.system.role.dto;

import lombok.Data;

@Data
public class RoleUpdateRequest {
    private String name;
    private Integer sort;
    private Integer status;
    private String remark;
}