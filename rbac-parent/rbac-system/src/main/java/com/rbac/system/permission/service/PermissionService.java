package com.rbac.system.permission.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rbac.system.permission.dto.*;
import com.rbac.system.permission.entity.SysPermission;
import java.util.List;

public interface PermissionService extends IService<SysPermission> {
    List<SysPermission> getTree();
    Long createPermission(PermissionCreateRequest request);
    void updatePermission(Long id, PermissionUpdateRequest request);
    void deletePermission(Long id);
}