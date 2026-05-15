package com.rbac.system.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rbac.common.exception.BadRequestException;
import com.rbac.system.role.entity.SysRole;
import com.rbac.system.role.service.RoleService;
import com.rbac.system.user.dto.*;
import com.rbac.system.user.entity.SysUser;
import com.rbac.system.user.entity.SysUserRole;
import com.rbac.system.user.mapper.SysUserMapper;
import com.rbac.system.user.mapper.SysUserRoleMapper;
import com.rbac.system.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements UserService {
    private final SysUserRoleMapper userRoleMapper;
    private final RoleService roleService;

    @Override
    public SysUser getByUsername(String username) {
        return getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
    }

    @Override
    public IPage<UserPageVO> page(UserPageRequest request) {
        Page<SysUser> page = new Page<>(request.getPage(), request.getSize());
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(request.getKeyword())) {
            wrapper.like(SysUser::getUsername, request.getKeyword()).or()
                   .like(SysUser::getRealName, request.getKeyword()).or()
                   .like(SysUser::getEmail, request.getKeyword());
        }
        if (request.getStatus() != null) {
            wrapper.eq(SysUser::getStatus, request.getStatus());
        }
        if (request.getDeptId() != null) {
            wrapper.eq(SysUser::getDeptId, request.getDeptId());
        }
        wrapper.orderByAsc(SysUser::getCreateTime);

        IPage<SysUser> userPage = page(page, wrapper);
        Page<UserPageVO> voPage = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        voPage.setRecords(userPage.getRecords().stream().map(this::toPageVO).collect(Collectors.toList()));
        return voPage;
    }

    private UserPageVO toPageVO(SysUser user) {
        UserPageVO vo = new UserPageVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setEmail(user.getEmail());
        vo.setPhone(user.getPhone());
        vo.setAvatar(user.getAvatar());
        vo.setStatus(user.getStatus());
        vo.setDeptId(user.getDeptId());
        vo.setCreateBy(user.getCreateBy());
        vo.setCreateTime(user.getCreateTime());
        vo.setUpdateBy(user.getUpdateBy());
        vo.setUpdateTime(user.getUpdateTime());
        vo.setLastLoginIp(user.getLastLoginIp());
        vo.setLastLoginTime(user.getLastLoginTime());

        List<SysRole> roles = roleService.listByIds(getUserRoleIds(user.getId()));
        vo.setRoleNames(roles.stream().map(SysRole::getName).collect(Collectors.toList()));
        return vo;
    }

    @Override
    @Transactional
    public Long createUser(UserCreateRequest request) {
        SysUser exist = getByUsername(request.getUsername());
        if (exist != null) {
            throw new BadRequestException("用户名已存在");
        }
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(new BCryptPasswordEncoder().encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setAvatar(request.getAvatar());
        user.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        user.setDeptId(request.getDeptId());
        save(user);
        return user.getId();
    }

    @Override
    @Transactional
    public void updateUser(Long id, UserUpdateRequest request) {
        SysUser user = getById(id);
        if (user == null) {
            throw new BadRequestException("用户不存在");
        }
        if (request.getRealName() != null) user.setRealName(request.getRealName());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getAvatar() != null) user.setAvatar(request.getAvatar());
        if (request.getStatus() != null) user.setStatus(request.getStatus());
        if (request.getDeptId() != null) user.setDeptId(request.getDeptId());
        updateById(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        SysUser user = getById(id);
        if (user == null) {
            throw new BadRequestException("用户不存在");
        }
        removeById(id);
        userRoleMapper.delete(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, id));
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        SysUser user = getById(id);
        if (user == null) {
            throw new BadRequestException("用户不存在");
        }
        user.setStatus(status);
        updateById(user);
    }

    @Override
    public List<Long> getUserRoleIds(Long userId) {
        return userRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId))
                .stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void assignRoles(Long userId, List<Long> roleIds) {
        SysUser user = getById(userId);
        if (user == null) {
            throw new BadRequestException("用户不存在");
        }
        userRoleMapper.delete(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));
        if (roleIds != null && !roleIds.isEmpty()) {
            roleIds.forEach(rid -> {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(userId);
                ur.setRoleId(rid);
                userRoleMapper.insert(ur);
            });
        }
    }
}