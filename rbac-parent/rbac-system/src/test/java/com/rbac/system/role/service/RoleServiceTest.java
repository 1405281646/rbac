package com.rbac.system.role.service;

import com.rbac.common.exception.BadRequestException;
import com.rbac.system.role.dto.RoleCreateRequest;
import com.rbac.system.role.dto.RoleUpdateRequest;
import com.rbac.system.role.entity.SysRole;
import com.rbac.system.role.mapper.SysRoleMapper;
import com.rbac.system.role.mapper.SysRolePermissionMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {
    @Mock private SysRoleMapper roleMapper;
    @Mock private SysRolePermissionMapper rolePermissionMapper;
    @InjectMocks private RoleServiceImpl roleService;

    @Test
    void deleteRole_withBuiltInRole_shouldThrowException() {
        SysRole role = new SysRole();
        role.setId(1L);
        role.setBuiltIn(1);
        when(roleMapper.selectById(1L)).thenReturn(role);

        assertThrows(BadRequestException.class, () -> roleService.deleteRole(1L));
    }

    @Test
    void deleteRole_withCustomRole_shouldSucceed() {
        SysRole role = new SysRole();
        role.setId(2L);
        role.setBuiltIn(0);
        when(roleMapper.selectById(2L)).thenReturn(role);

        assertDoesNotThrow(() -> roleService.deleteRole(2L));
        verify(roleMapper).deleteById(2L);
    }

    @Test
    void createRole_shouldSetDefaultValues() {
        RoleCreateRequest request = new RoleCreateRequest();
        request.setName("测试角色");
        request.setCode("test");

        Long id = roleService.createRole(request);
        assertNotNull(id);
        verify(roleMapper).insert(any(SysRole.class));
    }
}