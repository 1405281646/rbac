package com.rbac.system.user.service;

import com.rbac.common.exception.BadRequestException;
import com.rbac.system.role.service.RoleService;
import com.rbac.system.user.dto.UserCreateRequest;
import com.rbac.system.user.dto.UserUpdateRequest;
import com.rbac.system.user.entity.SysUser;
import com.rbac.system.user.entity.SysUserRole;
import com.rbac.system.user.mapper.SysUserMapper;
import com.rbac.system.user.mapper.SysUserRoleMapper;
import com.rbac.system.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock private SysUserMapper userMapper;
    @Mock private SysUserRoleMapper userRoleMapper;
    @Mock private RoleService roleService;
    @InjectMocks private UserServiceImpl userService;

    @Test
    void createUser_withDuplicateUsername_shouldThrowException() {
        SysUser exist = new SysUser();
        exist.setUsername("admin");
        when(userMapper.selectOne(any())).thenReturn(exist);

        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("admin");
        request.setPassword("pwd");

        assertThrows(BadRequestException.class, () -> userService.createUser(request));
    }

    @Test
    void createUser_withValidRequest_shouldSucceed() {
        when(userMapper.selectOne(any())).thenReturn(null);

        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("newuser");
        request.setPassword("password123");
        request.setRealName("新用户");

        Long id = userService.createUser(request);
        assertNotNull(id);
        verify(userMapper).insert(any(SysUser.class));
    }

    @Test
    void deleteUser_withNonExistentUser_shouldThrowException() {
        when(userMapper.selectById(99L)).thenReturn(null);

        assertThrows(BadRequestException.class, () -> userService.deleteUser(99L));
    }

    @Test
    void deleteUser_withExistingUser_shouldSucceed() {
        SysUser user = new SysUser();
        user.setId(1L);
        user.setUsername("test");
        when(userMapper.selectById(1L)).thenReturn(user);

        assertDoesNotThrow(() -> userService.deleteUser(1L));
        verify(userMapper).deleteById(1L);
    }

    @Test
    void assignRoles_shouldReplaceAllRoles() {
        SysUser user = new SysUser();
        user.setId(1L);
        when(userMapper.selectById(1L)).thenReturn(user);

        userService.assignRoles(1L, List.of(10L, 20L));

        verify(userRoleMapper).delete(any());
        verify(userRoleMapper, times(2)).insert(any(SysUserRole.class));
    }
}