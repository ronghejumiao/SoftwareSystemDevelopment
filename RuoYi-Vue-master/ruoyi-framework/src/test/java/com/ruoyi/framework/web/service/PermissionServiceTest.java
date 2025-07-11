// 100%方法覆盖率版的 PermissionServiceTest.java
package com.ruoyi.framework.web.service;

import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.framework.security.context.PermissionContextHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 针对 PermissionService 的单元测试类 (100%方法覆盖率版)
 *
 * @author Gemini
 */
@ExtendWith(MockitoExtension.class)
class PermissionServiceTest {

    @InjectMocks
    private PermissionService permissionService;

    // 静态类的模拟对象
    private MockedStatic<SecurityUtils> securityUtilsMockedStatic;
    private MockedStatic<PermissionContextHolder> permissionContextHolderMockedStatic;

    // 预设一个测试用的LoginUser
    private LoginUser loginUser;
    private SysUser sysUser;

    @BeforeEach
    void setUp() {
        // 在每个测试前都初始化静态模拟
        securityUtilsMockedStatic = mockStatic(SecurityUtils.class);
        permissionContextHolderMockedStatic = mockStatic(PermissionContextHolder.class);

        // 创建一个基础的、可供各测试修改的用户对象
        sysUser = new SysUser();
        sysUser.setRoles(new ArrayList<>());
        loginUser = new LoginUser(1L, 1L, sysUser, new HashSet<>());

        // 默认情况下，让 SecurityUtils.getLoginUser() 返回我们创建的测试用户
        securityUtilsMockedStatic.when(SecurityUtils::getLoginUser).thenReturn(loginUser);

        // 确保对 PermissionContextHolder.setContext 的调用不会产生任何影响
        permissionContextHolderMockedStatic.when(() -> PermissionContextHolder.setContext(anyString())).thenAnswer(invocation -> null);
    }

    @AfterEach
    void tearDown() {
        // 在每个测试后必须关闭静态模拟，以防测试间互相干扰
        securityUtilsMockedStatic.close();
        permissionContextHolderMockedStatic.close();
    }

    @Nested
    @DisplayName("hasPermi 方法测试")
    class HasPermiTest {
        @Test
        @DisplayName("当权限字符串为空时应返回 false")
        void shouldReturnFalse_WhenPermissionIsEmpty() {
            assertFalse(permissionService.hasPermi(null));
            assertFalse(permissionService.hasPermi(""));
        }

        @Test
        @DisplayName("当登录用户为 null 时应返回 false")
        void shouldReturnFalse_WhenLoginUserIsNull() {
            securityUtilsMockedStatic.when(SecurityUtils::getLoginUser).thenReturn(null);
            assertFalse(permissionService.hasPermi("system:user:list"));
        }

        @Test
        @DisplayName("当用户权限集合为空时应返回 false")
        void shouldReturnFalse_WhenUserPermissionsAreEmpty() {
            loginUser.setPermissions(new HashSet<>());
            assertFalse(permissionService.hasPermi("system:user:list"));
        }

        @Test
        @DisplayName("当用户拥有指定权限时应返回 true")
        void shouldReturnTrue_WhenUserHasThePermission() {
            loginUser.getPermissions().add("system:user:list");
            assertTrue(permissionService.hasPermi("system:user:list"));
        }

        @Test
        @DisplayName("当用户拥有全部权限(admin)时应返回 true")
        void shouldReturnTrue_WhenUserHasAllPermission() {
            loginUser.getPermissions().add(Constants.ALL_PERMISSION);
            assertTrue(permissionService.hasPermi("some:other:permission"));
        }

        @Test
        @DisplayName("当用户不拥有指定权限时应返回 false")
        void shouldReturnFalse_WhenUserLacksThePermission() {
            loginUser.getPermissions().add("system:user:add");
            assertFalse(permissionService.hasPermi("system:user:list"));
        }
    }

    // 【方法覆盖率补充】为 lacksPermi 方法添加测试
    @Nested
    @DisplayName("lacksPermi 方法测试")
    class LacksPermiTest {
        @Test
        @DisplayName("当用户没有权限时应返回 true")
        void shouldReturnTrue_WhenUserLacksPermission() {
            assertTrue(permissionService.lacksPermi("system:user:list"));
        }

        @Test
        @DisplayName("当用户拥有权限时应返回 false")
        void shouldReturnFalse_WhenUserHasPermission() {
            loginUser.getPermissions().add("system:user:list");
            assertFalse(permissionService.lacksPermi("system:user:list"));
        }
    }

    // 【方法覆盖率补充】为 hasAnyPermi 方法添加测试
    @Nested
    @DisplayName("hasAnyPermi 方法测试")
    class HasAnyPermiTest {
        @Test
        @DisplayName("当权限字符串为空时应返回 false")
        void shouldReturnFalse_WhenPermissionsStringIsEmpty() {
            assertFalse(permissionService.hasAnyPermi(null));
            assertFalse(permissionService.hasAnyPermi(""));
        }

        @Test
        @DisplayName("当用户拥有其中一个权限时应返回 true")
        void shouldReturnTrue_WhenUserHasOneOfThePermissions() {
            loginUser.getPermissions().add("system:user:edit");
            assertTrue(permissionService.hasAnyPermi("system:user:add,system:user:edit,system:user:delete"));
        }

        @Test
        @DisplayName("当用户一个权限都没有时应返回 false")
        void shouldReturnFalse_WhenUserHasNoneOfThePermissions() {
            loginUser.getPermissions().add("system:role:list");
            assertFalse(permissionService.hasAnyPermi("system:user:add,system:user:edit"));
        }

        @Test
        @DisplayName("当用户拥有全部权限(admin)时应返回 true")
        void shouldReturnTrue_WhenUserHasAllPermission() {
            loginUser.getPermissions().add(Constants.ALL_PERMISSION);
            assertTrue(permissionService.hasAnyPermi("system:user:add,system:user:edit"));
        }
    }

    @Nested
    @DisplayName("hasRole 方法测试")
    class HasRoleTest {
        @Test
        @DisplayName("当角色字符串为空时应返回 false")
        void shouldReturnFalse_WhenRoleIsEmpty() {
            assertFalse(permissionService.hasRole(null));
            assertFalse(permissionService.hasRole(""));
        }

        @Test
        @DisplayName("当登录用户为 null 时应返回 false")
        void shouldReturnFalse_WhenLoginUserIsNull() {
            securityUtilsMockedStatic.when(SecurityUtils::getLoginUser).thenReturn(null);
            assertFalse(permissionService.hasRole("admin"));
        }

        @Test
        @DisplayName("当用户角色集合为空时应返回 false")
        void shouldReturnFalse_WhenUserRolesAreEmpty() {
            assertTrue(sysUser.getRoles().isEmpty());
            assertFalse(permissionService.hasRole("admin"));
        }

        @Test
        @DisplayName("当用户是超级管理员时应返回 true")
        void shouldReturnTrue_WhenUserIsSuperAdmin() {
            SysRole adminRole = new SysRole();
            adminRole.setRoleKey(Constants.SUPER_ADMIN);
            sysUser.getRoles().add(adminRole);

            assertTrue(permissionService.hasRole("someOtherRole"));
        }

        @Test
        @DisplayName("当用户拥有指定角色时应返回 true")
        void shouldReturnTrue_WhenUserHasTheRole() {
            SysRole commonRole = new SysRole();
            commonRole.setRoleKey("common");
            sysUser.getRoles().add(commonRole);

            assertTrue(permissionService.hasRole("common"));
        }
    }

    // 【方法覆盖率补充】为 lacksRole 方法添加测试
    @Nested
    @DisplayName("lacksRole 方法测试")
    class LacksRoleTest {
        @Test
        @DisplayName("当用户没有角色时应返回 true")
        void shouldReturnTrue_WhenUserLacksRole() {
            assertTrue(permissionService.lacksRole("admin"));
        }

        @Test
        @DisplayName("当用户拥有角色时应返回 false")
        void shouldReturnFalse_WhenUserHasRole() {
            SysRole adminRole = new SysRole();
            adminRole.setRoleKey("admin");
            sysUser.getRoles().add(adminRole);

            assertFalse(permissionService.lacksRole("admin"));
        }
    }

    @Nested
    @DisplayName("hasAnyRoles 方法测试")
    class HasAnyRolesTest {
        @Test
        @DisplayName("当角色字符串为空时应返回 false")
        void shouldReturnFalse_WhenRolesStringIsEmpty() {
            assertFalse(permissionService.hasAnyRoles(null));
            assertFalse(permissionService.hasAnyRoles(""));
        }

        @Test
        @DisplayName("当用户拥有其中一个角色时应返回 true")
        void shouldReturnTrue_WhenUserHasOneOfTheRoles() {
            SysRole commonRole = new SysRole();
            commonRole.setRoleKey("common");
            sysUser.getRoles().add(commonRole);

            assertTrue(permissionService.hasAnyRoles("admin,common,tester"));
        }

        @Test
        @DisplayName("当用户一个角色都没有时应返回 false")
        void shouldReturnFalse_WhenUserHasNoneOfTheRoles() {
            SysRole otherRole = new SysRole();
            otherRole.setRoleKey("other");
            sysUser.getRoles().add(otherRole);

            assertFalse(permissionService.hasAnyRoles("admin,common"));
        }
    }
}