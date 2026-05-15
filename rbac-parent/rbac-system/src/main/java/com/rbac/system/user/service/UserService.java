package com.rbac.system.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rbac.system.user.dto.*;
import com.rbac.system.user.entity.SysUser;
import java.util.List;

public interface UserService extends IService<SysUser> {
    SysUser getByUsername(String username);
    IPage<UserPageVO> page(UserPageRequest request);
    Long createUser(UserCreateRequest request);
    void updateUser(Long id, UserUpdateRequest request);
    void deleteUser(Long id);
    void updateStatus(Long id, Integer status);
    List<Long> getUserRoleIds(Long userId);
    void assignRoles(Long userId, List<Long> roleIds);
}