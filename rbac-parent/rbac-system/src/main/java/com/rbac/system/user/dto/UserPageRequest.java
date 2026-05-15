package com.rbac.system.user.dto;

import lombok.Data;

@Data
public class UserPageRequest {
    private int page = 1;
    private int size = 10;
    private String keyword;
    private Integer status;
    private Long deptId;
}