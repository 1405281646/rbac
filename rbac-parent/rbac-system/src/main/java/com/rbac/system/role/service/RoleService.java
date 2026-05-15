package com.rbac.system.role.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rbac.system.role.dto.*;
import com.rbac.system.role.entity.SysRole;
import java.util.List;

public interface RoleService extends IService<SysRole> {
    List<SysRole> getTree();
    IPage<SysRole> page(RolePageRequest request);
    Long createRole(RoleCreateRequest request);
    void updateRole(Long id, RoleUpdateRequest request);
    void deleteRole(Long id);
    void updateStatus(Long id, Integer status);
    List<Long> getRolePermissionIds(Long roleId);
    void assignPermissions(Long roleId, List<Long> permissionIds);
}