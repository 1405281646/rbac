package com.rbac.api.controller;

import com.rbac.common.response.PageResult;
import com.rbac.common.response.Result;
import com.rbac.system.user.dto.*;
import com.rbac.system.user.entity.SysUser;
import com.rbac.system.user.service.UserService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/system/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/page")
    public Result<PageResult<UserPageVO>> page(@ModelAttribute UserPageRequest request) {
        IPage<UserPageVO> page = userService.page(request);
        PageResult<UserPageVO> result = PageResult.of(
                page.getRecords(), page.getTotal(), (int) page.getCurrent(), (int) page.getSize());
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<SysUser> get(@PathVariable Long id) {
        return Result.success(userService.getById(id));
    }

    @PostMapping
    public Result<Long> create(@Valid @RequestBody UserCreateRequest request) {
        return Result.success(userService.createUser(request));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        userService.updateUser(id, request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> status(@PathVariable Long id, @RequestBody Integer status) {
        userService.updateStatus(id, status);
        return Result.success();
    }

    @GetMapping("/{id}/roles")
    public Result<List<Long>> roles(@PathVariable Long id) {
        return Result.success(userService.getUserRoleIds(id));
    }

    @PutMapping("/{id}/roles")
    public Result<Void> roles(@PathVariable Long id, @RequestBody List<Long> roleIds) {
        userService.assignRoles(id, roleIds);
        return Result.success();
    }
}