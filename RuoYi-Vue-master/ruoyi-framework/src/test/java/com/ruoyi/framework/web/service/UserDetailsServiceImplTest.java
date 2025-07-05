package com.ruoyi.framework.web.service;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.enums.UserStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.exception.user.UserPasswordNotMatchException;
import com.ruoyi.common.utils.MessageUtils;
import com.ruoyi.system.service.ISysUserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * 针对 UserDetailsServiceImpl 的单元测试类
 *
 * @author Gemini
 */
@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    // --- 模拟所有依赖项 ---
    @Mock
    private ISysUserService userService;
    @Mock
    private SysPasswordService passwordService;
    @Mock
    private SysPermissionService permissionService;

    // --- 模拟静态类 ---
    private MockedStatic<MessageUtils> messageUtilsMockedStatic;

    private SysUser testUser;
    private final String TEST_USERNAME = "testuser";

    @BeforeEach
    void setUp() {
        // 初始化静态模拟
        messageUtilsMockedStatic = mockStatic(MessageUtils.class);

        // 设置 MessageUtils.message 的默认行为，直接返回消息代码
        when(MessageUtils.message(anyString(), any())).thenAnswer(invocation -> invocation.getArgument(0));

        // 创建一个基础的、状态正常的测试用户
        testUser = new SysUser();
        testUser.setUserId(1L);
        testUser.setDeptId(100L);
        testUser.setUserName(TEST_USERNAME);
        testUser.setDelFlag("0"); // 未删除
        testUser.setStatus("0");  // 正常状态
    }

    @AfterEach
    void tearDown() {
        // 关闭静态模拟
        messageUtilsMockedStatic.close();
    }

    @Test
    @DisplayName("加载用户成功：当用户有效时，应返回UserDetails对象")
    void loadUserByUsername_WithValidUser_ShouldReturnUserDetails() {
        // Arrange (准备)
        Set<String> permissions = new HashSet<>();
        permissions.add("system:user:list");

        // 模拟依赖方法的行为
        when(userService.selectUserByUserName(TEST_USERNAME)).thenReturn(testUser);
        doNothing().when(passwordService).validate(testUser); // 模拟密码验证通过
        when(permissionService.getMenuPermission(testUser)).thenReturn(permissions);

        // Act (执行)
        UserDetails userDetails = userDetailsService.loadUserByUsername(TEST_USERNAME);

        // Assert (断言)
        assertNotNull(userDetails);
        assertTrue(userDetails instanceof LoginUser);
        LoginUser loginUser = (LoginUser) userDetails;

        assertEquals(testUser.getUserId(), loginUser.getUserId());
        assertEquals(testUser.getUserName(), loginUser.getUsername());
        assertEquals(permissions, loginUser.getPermissions());

        // 验证所有依赖都被正确调用
        verify(userService, times(1)).selectUserByUserName(TEST_USERNAME);
        verify(passwordService, times(1)).validate(testUser);
        verify(permissionService, times(1)).getMenuPermission(testUser);
    }

    @Test
    @DisplayName("加载用户失败：当用户不存在时，应抛出ServiceException")
    void loadUserByUsername_WhenUserNotFound_ShouldThrowServiceException() {
        // Arrange
        when(userService.selectUserByUserName(TEST_USERNAME)).thenReturn(null);

        // Act & Assert
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            userDetailsService.loadUserByUsername(TEST_USERNAME);
        });

        assertEquals("user.not.exists", exception.getMessage());
        // 验证 passwordService 和 permissionService 都未被调用
        verify(passwordService, never()).validate(any());
        verify(permissionService, never()).getMenuPermission(any());
    }

    @Test
    @DisplayName("加载用户失败：当用户已被删除时，应抛出ServiceException")
    void loadUserByUsername_WhenUserIsDeleted_ShouldThrowServiceException() {
        // Arrange
        testUser.setDelFlag(UserStatus.DELETED.getCode()); // 设置为已删除状态
        when(userService.selectUserByUserName(TEST_USERNAME)).thenReturn(testUser);

        // Act & Assert
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            userDetailsService.loadUserByUsername(TEST_USERNAME);
        });

        assertEquals("user.password.delete", exception.getMessage());
        verify(passwordService, never()).validate(any());
    }

    @Test
    @DisplayName("加载用户失败：当用户已被停用时，应抛出ServiceException")
    void loadUserByUsername_WhenUserIsDisabled_ShouldThrowServiceException() {
        // Arrange
        testUser.setStatus(UserStatus.DISABLE.getCode()); // 设置为停用状态
        when(userService.selectUserByUserName(TEST_USERNAME)).thenReturn(testUser);

        // Act & Assert
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            userDetailsService.loadUserByUsername(TEST_USERNAME);
        });

        assertEquals("user.blocked", exception.getMessage());
        verify(passwordService, never()).validate(any());
    }

    @Test
    @DisplayName("加载用户失败：当密码校验失败时，应抛出相应异常")
    void loadUserByUsername_WhenPasswordValidationFails_ShouldThrowException() {
        // Arrange
        when(userService.selectUserByUserName(TEST_USERNAME)).thenReturn(testUser);
        // 模拟 passwordService.validate 抛出密码不匹配异常
        doThrow(new UserPasswordNotMatchException()).when(passwordService).validate(testUser);

        // Act & Assert
        assertThrows(UserPasswordNotMatchException.class, () -> {
            userDetailsService.loadUserByUsername(TEST_USERNAME);
        });

        // 验证 permissionService 从未被调用，因为在密码校验阶段就已失败
        verify(permissionService, never()).getMenuPermission(any());
    }
}