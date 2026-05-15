package com.rbac.system.role.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rbac.common.exception.BadRequestException;
import com.rbac.system.role.dto.*;
import com.rbac.system.role.entity.SysRole;
import com.rbac.system.role.entity.SysRolePermission;
import com.rbac.system.role.mapper.SysRoleMapper;
import com.rbac.system.role.mapper.SysRolePermissionMapper;
import com.rbac.system.role.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements RoleService {
    private final SysRolePermissionMapper rolePermissionMapper;

    @Override
    public List<SysRole> getTree() {
        List<SysRole> all = list(new LambdaQueryWrapper<SysRole>().orderByAsc(SysRole::getSort));
        return buildTree(all, 0L);
    }

    private List<SysRole> buildTree(List<SysRole> all, Long parentId) {
        return all.stream()
                .filter(r -> r.getParentId() != null && r.getParentId().equals(parentId))
                .peek(r -> r.setChildren(buildTree(all, r.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public IPage<SysRole> page(RolePageRequest request) {
        Page<SysRole> page = new Page<>(request.getPage(), request.getSize());
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(request.getKeyword())) {
            wrapper.like(SysRole::getName, request.getKeyword()).or()
                   .like(SysRole::getCode, request.getKeyword());
        }
        if (request.getStatus() != null) {
            wrapper.eq(SysRole::getStatus, request.getStatus());
        }
        wrapper.orderByAsc(SysRole::getSort);
        return page(page, wrapper);
    }

    @Override
    @Transactional
    public Long createRole(RoleCreateRequest request) {
        SysRole role = new SysRole();
        role.setName(request.getName());
        role.setCode(request.getCode());
        role.setParentId(request.getParentId() != null ? request.getParentId() : 0L);
        role.setSort(request.getSort() != null ? request.getSort() : 0);
        role.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        role.setRemark(request.getRemark());
        save(role);
        return role.getId();
    }

    @Override
    @Transactional
    public void updateRole(Long id, RoleUpdateRequest request) {
        SysRole role = getById(id);
        if (role == null) {
            throw new BadRequestException("角色不存在");
        }
        if (role.getBuiltIn() == 1) {
            throw new BadRequestException("系统内置角色不可修改");
        }
        if (request.getName() != null) role.setName(request.getName());
        if (request.getSort() != null) role.setSort(request.getSort());
        if (request.getStatus() != null) role.setStatus(request.getStatus());
        if (request.getRemark() != null) role.setRemark(request.getRemark());
        updateById(role);
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        SysRole role = getById(id);
        if (role != null && role.getBuiltIn() == 1) {
            throw new BadRequestException("系统内置角色不可删除");
        }
        removeById(id);
        rolePermissionMapper.delete(
                new LambdaQueryWrapper<SysRolePermission>().eq(SysRolePermission::getRoleId, id));
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        SysRole role = getById(id);
        if (role == null) {
            throw new BadRequestException("角色不存在");
        }
        role.setStatus(status);
        updateById(role);
    }

    @Override
    public List<Long> getRolePermissionIds(Long roleId) {
        return rolePermissionMapper.selectList(
                new LambdaQueryWrapper<SysRolePermission>().eq(SysRolePermission::getRoleId, roleId))
                .stream().map(SysRolePermission::getPermissionId).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        rolePermissionMapper.delete(
                new LambdaQueryWrapper<SysRolePermission>().eq(SysRolePermission::getRoleId, roleId));
        if (permissionIds != null && !permissionIds.isEmpty()) {
            permissionIds.forEach(pid -> {
                SysRolePermission rp = new SysRolePermission();
                rp.setRoleId(roleId);
                rp.setPermissionId(pid);
                rolePermissionMapper.insert(rp);
            });
        }
    }
}