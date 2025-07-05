package com.ruoyi.framework.web.service;

import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.framework.security.context.PermissionContextHolder;
import com.ruoyi.system.service.ISysMenuService;
import com.ruoyi.system.service.ISysRoleService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 针对 SysPermissionService 的单元测试类
 *
 * @author Gemini
 */
@ExtendWith(MockitoExtension.class)
class SysPermissionServiceTest {

    @InjectMocks
    private SysPermissionService sysPermissionService;

    @Mock
    private ISysRoleService roleService;

    @Mock
    private ISysMenuService menuService;

    private MockedStatic<SecurityUtils> securityUtilsMockedStatic;
    private MockedStatic<PermissionContextHolder> permissionContextHolderMockedStatic;

    private SysUser testUser;

    @BeforeEach
    void setUp() {
        securityUtilsMockedStatic = mockStatic(SecurityUtils.class);
        permissionContextHolderMockedStatic = mockStatic(PermissionContextHolder.class);

        testUser = new SysUser();
        LoginUser loginUser = new LoginUser(1L, 1L, testUser, new HashSet<>());
        testUser.setRoles(new ArrayList<>());
        securityUtilsMockedStatic.when(SecurityUtils::getLoginUser).thenReturn(loginUser);

        permissionContextHolderMockedStatic.when(() -> PermissionContextHolder.setContext(anyString())).thenAnswer(invocation -> null);
    }

    @AfterEach
    void tearDown() {
        securityUtilsMockedStatic.close();
        permissionContextHolderMockedStatic.close();
    }

    @Nested
    @DisplayName("getRolePermission 方法测试")
    class GetRolePermissionTest {
        // ... 此处测试用例保持不变 ...
        @Test
        @DisplayName("对于管理员用户，应返回 'admin' 角色")
        void forAdminUser_ShouldReturnAdminRole() {
            testUser.setUserId(1L);

            Set<String> roles = sysPermissionService.getRolePermission(testUser);

            assertNotNull(roles);
            assertEquals(1, roles.size());
            assertTrue(roles.contains("admin"));
            verify(roleService, never()).selectRolePermissionByUserId(anyLong());
        }
    }

    @Nested
    @DisplayName("getMenuPermission 方法测试")
    class GetMenuPermissionTest {
        // ... 此处测试用例保持不变 ...
        @Test
        @DisplayName("对于管理员用户，应返回 '*:*:*' 通配符权限")
        void forAdminUser_ShouldReturnAllPermissionsWildcard() {
            testUser.setUserId(1L);
            Set<String> perms = sysPermissionService.getMenuPermission(testUser);
            assertNotNull(perms);
            assertEquals(1, perms.size());
            assertTrue(perms.contains("*:*:*"));
            verify(menuService, never()).selectMenuPermsByUserId(anyLong());
        }

        @Test
        @DisplayName("对于没有角色的非管理员用户，应根据用户ID获取权限")
        void forNonAdminUserWithNoRoles_ShouldGetPermsByUserId() {
            testUser.setUserId(2L);
            testUser.setRoles(new ArrayList<>());
            Set<String> expectedPerms = new HashSet<>(Arrays.asList("system:user:list"));
            when(menuService.selectMenuPermsByUserId(2L)).thenReturn(expectedPerms);
            Set<String> perms = sysPermissionService.getMenuPermission(testUser);
            assertEquals(expectedPerms, perms);
            verify(menuService, times(1)).selectMenuPermsByUserId(2L);
            verify(menuService, never()).selectMenuPermsByRoleId(anyLong());
        }

        // 【修复】重写此测试用例以正确模拟 role.isAdmin() 的行为
        @Test
        @DisplayName("对于有角色的非管理员用户，应聚合所有有效角色的权限")
        void forNonAdminUserWithRoles_ShouldAggregatePermsFromValidRoles() {
            // Arrange
            testUser.setUserId(2L); // 非管理员用户

            // 角色1: 正常有效的角色
            SysRole normalRole = new SysRole(101L);
            normalRole.setStatus(UserConstants.ROLE_NORMAL);

            // 角色2: 已停用的角色
            SysRole disabledRole = new SysRole(102L);
            disabledRole.setStatus(UserConstants.ROLE_DISABLE);

            // 角色3: 超级管理员角色 (ID为1)
            SysRole adminRole = new SysRole(1L);
            adminRole.setStatus(UserConstants.ROLE_NORMAL);

            // 设置用户的角色列表
            testUser.setRoles(Arrays.asList(normalRole, disabledRole, adminRole));

            // 模拟menuService的行为：只为正常角色ID(101)返回权限
            Set<String> normalRolePerms = new HashSet<>(Arrays.asList("system:user:list", "system:user:view"));
            when(menuService.selectMenuPermsByRoleId(101L)).thenReturn(normalRolePerms);

            // Act
            Set<String> perms = sysPermissionService.getMenuPermission(testUser);

            // Assert
            // 1. 最终权限集应只包含正常角色的权限
            assertEquals(normalRolePerms, perms);

            // 2. 验证menuService只被正常角色ID(101)调用了一次
            verify(menuService, times(1)).selectMenuPermsByRoleId(101L);

            // 3. 验证menuService从未被停用角色ID(102)或管理员角色ID(1)调用
            verify(menuService, never()).selectMenuPermsByRoleId(102L);
            verify(menuService, never()).selectMenuPermsByRoleId(1L);

            // 4. 验证从未根据用户ID查询权限（因为用户有关联的角色）
            verify(menuService, never()).selectMenuPermsByUserId(anyLong());
        }
    }
}