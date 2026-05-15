package com.rbac.api.controller;

import com.rbac.common.response.Result;
import com.rbac.system.permission.dto.*;
import com.rbac.system.permission.entity.SysPermission;
import com.rbac.system.permission.service.PermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/system/permission")
@RequiredArgsConstructor
public class PermissionController {
    private final PermissionService permissionService;

    @GetMapping("/tree")
    public Result<List<SysPermission>> tree() {
        return Result.success(permissionService.getTree());
    }

    @GetMapping("/{id}")
    public Result<SysPermission> get(@PathVariable Long id) {
        return Result.success(permissionService.getById(id));
    }

    @PostMapping
    public Result<Long> create(@Valid @RequestBody PermissionCreateRequest request) {
        return Result.success(permissionService.createPermission(request));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody PermissionUpdateRequest request) {
        permissionService.updatePermission(id, request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        permissionService.deletePermission(id);
        return Result.success();
    }
}