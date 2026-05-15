package com.rbac.system.user.entity;

import lombok.Data;
import java.io.Serializable;

@Data
public class SysUserRole implements Serializable {
    private Long userId;
    private Long roleId;
}