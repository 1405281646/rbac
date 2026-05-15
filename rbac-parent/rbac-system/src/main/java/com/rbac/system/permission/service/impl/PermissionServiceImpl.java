package com.rbac.system.permission.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rbac.common.exception.BadRequestException;
import com.rbac.system.permission.dto.*;
import com.rbac.system.permission.entity.SysPermission;
import com.rbac.system.permission.mapper.SysPermissionMapper;
import com.rbac.system.permission.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements PermissionService {

    @Override
    public List<SysPermission> getTree() {
        List<SysPermission> all = list(new LambdaQueryWrapper<SysPermission>().orderByAsc(SysPermission::getSort));
        return buildTree(all, 0L);
    }

    private List<SysPermission> buildTree(List<SysPermission> all, Long parentId) {
        return all.stream()
                .filter(p -> p.getParentId() != null && p.getParentId().equals(parentId))
                .peek(p -> p.setChildren(buildTree(all, p.getId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Long createPermission(PermissionCreateRequest request) {
        SysPermission perm = new SysPermission();
        perm.setName(request.getName());
        perm.setType(request.getType());
        perm.setParentId(request.getParentId() != null ? request.getParentId() : 0L);
        perm.setPath(request.getPath());
        perm.setComponent(request.getComponent());
        perm.setPerms(request.getPerms());
        perm.setIcon(request.getIcon());
        perm.setSort(request.getSort() != null ? request.getSort() : 0);
        perm.setVisible(request.getVisible() != null ? request.getVisible() : 1);
        save(perm);
        return perm.getId();
    }

    @Override
    @Transactional
    public void updatePermission(Long id, PermissionUpdateRequest request) {
        SysPermission perm = getById(id);
        if (perm == null) {
            throw new BadRequestException("权限不存在");
        }
        if (request.getName() != null) perm.setName(request.getName());
        if (request.getType() != null) perm.setType(request.getType());
        if (request.getParentId() != null) perm.setParentId(request.getParentId());
        if (request.getPath() != null) perm.setPath(request.getPath());
        if (request.getComponent() != null) perm.setComponent(request.getComponent());
        if (request.getPerms() != null) perm.setPerms(request.getPerms());
        if (request.getIcon() != null) perm.setIcon(request.getIcon());
        if (request.getSort() != null) perm.setSort(request.getSort());
        if (request.getVisible() != null) perm.setVisible(request.getVisible());
        updateById(perm);
    }

    @Override
    @Transactional
    public void deletePermission(Long id) {
        List<SysPermission> children = list(new LambdaQueryWrapper<SysPermission>().eq(SysPermission::getParentId, id));
        if (!children.isEmpty()) {
            throw new BadRequestException("存在子权限，无法删除");
        }
        removeById(id);
    }
}