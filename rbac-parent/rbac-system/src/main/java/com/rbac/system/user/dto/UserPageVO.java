package com.rbac.system.user.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserPageVO {
    private Long id;
    private String username;
    private String realName;
    private String email;
    private String phone;
    private String avatar;
    private Integer status;
    private Long deptId;
    private String createBy;
    private LocalDateTime createTime;
    private String updateBy;
    private LocalDateTime updateTime;
    private String lastLoginIp;
    private LocalDateTime lastLoginTime;
    private List<String> roleNames;
}