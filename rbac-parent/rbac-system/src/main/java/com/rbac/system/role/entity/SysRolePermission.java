package com.rbac.system.role.entity;

import lombok.Data;
import java.io.Serializable;

@Data
public class SysRolePermission implements Serializable {
    private Long roleId;
    private Long permissionId;
}