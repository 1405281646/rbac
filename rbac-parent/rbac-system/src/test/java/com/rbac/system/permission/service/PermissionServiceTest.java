package com.rbac.system.permission.service;

import com.rbac.common.exception.BadRequestException;
import com.rbac.system.permission.dto.PermissionCreateRequest;
import com.rbac.system.permission.entity.SysPermission;
import com.rbac.system.permission.mapper.SysPermissionMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PermissionServiceTest {
    @Mock private SysPermissionMapper permissionMapper;
    @InjectMocks private PermissionServiceImpl permissionService;

    @Test
    void deletePermission_withChildren_shouldThrowException() {
        SysPermission child = new SysPermission();
        child.setId(2L);
        child.setParentId(1L);
        when(permissionMapper.selectList(any())).thenReturn(List.of(child));

        assertThrows(BadRequestException.class, () -> permissionService.deletePermission(1L));
    }

    @Test
    void deletePermission_withoutChildren_shouldSucceed() {
        when(permissionMapper.selectList(any())).thenReturn(List.of());

        assertDoesNotThrow(() -> permissionService.deletePermission(1L));
        verify(permissionMapper).deleteById(1L);
    }

    @Test
    void createPermission_shouldSetDefaultParentId() {
        PermissionCreateRequest request = new PermissionCreateRequest();
        request.setName("测试权限");
        request.setType(2);
        request.setPerms("test:perm");

        Long id = permissionService.createPermission(request);
        assertNotNull(id);
        verify(permissionMapper).insert(argThat(p -> p.getParentId() == 0L));
    }
}