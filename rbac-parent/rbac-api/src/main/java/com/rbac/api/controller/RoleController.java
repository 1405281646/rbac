package com.rbac.api.controller;

import com.rbac.common.response.PageResult;
import com.rbac.common.response.Result;
import com.rbac.system.role.dto.*;
import com.rbac.system.role.entity.SysRole;
import com.rbac.system.role.service.RoleService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/system/role")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @GetMapping("/tree")
    public Result<List<SysRole>> tree() {
        return Result.success(roleService.getTree());
    }

    @GetMapping("/page")
    public Result<PageResult<SysRole>> page(@ModelAttribute RolePageRequest request) {
        IPage<SysRole> page = roleService.page(request);
        PageResult<SysRole> result = PageResult.of(
                page.getRecords(), page.getTotal(), (int) page.getCurrent(), (int) page.getSize());
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<SysRole> get(@PathVariable Long id) {
        return Result.success(roleService.getById(id));
    }

    @PostMapping
    public Result<Long> create(@Valid @RequestBody RoleCreateRequest request) {
        return Result.success(roleService.createRole(request));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody RoleUpdateRequest request) {
        roleService.updateRole(id, request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roleService.deleteRole(id);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> status(@PathVariable Long id, @RequestBody Integer status) {
        roleService.updateStatus(id, status);
        return Result.success();
    }

    @GetMapping("/{id}/permissions")
    public Result<List<Long>> permissions(@PathVariable Long id) {
        return Result.success(roleService.getRolePermissionIds(id));
    }

    @PutMapping("/{id}/permissions")
    public Result<Void> permissions(@PathVariable Long id, @RequestBody List<Long> permissionIds) {
        roleService.assignPermissions(id, permissionIds);
        return Result.success();
    }
}