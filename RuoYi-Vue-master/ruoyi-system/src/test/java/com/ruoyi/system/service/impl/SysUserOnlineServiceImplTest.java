package com.ruoyi.system.service.impl;

import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.SysUserOnline;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.entity.SysDept;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SysUserOnlineServiceImplTest {

    private SysUserOnlineServiceImpl service;

    private MockedStatic<StringUtils> stringUtilsMockedStatic;

    @BeforeEach
    void setUp() {
        service = new SysUserOnlineServiceImpl();
        stringUtilsMockedStatic = mockStatic(StringUtils.class);
    }

    @AfterEach
    void tearDown() {
        stringUtilsMockedStatic.close();
    }

    @Test
    void testSelectOnlineByIpaddr_NotMatch() {
        LoginUser user = new LoginUser();
        user.setIpaddr("127.0.0.1");
        stringUtilsMockedStatic.when(() -> StringUtils.equals("192.168.1.1", "127.0.0.1")).thenReturn(false);

        SysUserOnline result = service.selectOnlineByIpaddr("192.168.1.1", user);
        assertNull(result);
    }

    @Test
    void testSelectOnlineByUserName_Match() {
        LoginUser user = new LoginUser();
        stringUtilsMockedStatic.when(() -> StringUtils.equals("admin", "admin")).thenReturn(true);
    }







    @Test
    void testLoginUserToUserOnline_NullUser() {
        stringUtilsMockedStatic.when(() -> StringUtils.isNull((Object) null)).thenReturn(true);
        assertNull(service.loginUserToUserOnline(null));
    }

    @Test
    void testLoginUserToUserOnline_NullSysUser() {
        LoginUser user = new LoginUser();
        stringUtilsMockedStatic.when(() -> StringUtils.isNull(user)).thenReturn(false);
        stringUtilsMockedStatic.when(() -> StringUtils.isNull(user.getUser())).thenReturn(true);
        assertNull(service.loginUserToUserOnline(user));
    }

    @Test
    void testLoginUserToUserOnline_Normal() {
        LoginUser user = new LoginUser();
        user.setToken("token123");
        user.setIpaddr("127.0.0.1");
        user.setLoginLocation("China");
        user.setBrowser("Chrome");
        user.setOs("Windows");
        user.setLoginTime(System.currentTimeMillis());

        SysUser sysUser = new SysUser();
        SysDept dept = new SysDept();
        dept.setDeptName("技术部");
        sysUser.setDept(dept);
        user.setUser(sysUser);

        stringUtilsMockedStatic.when(() -> StringUtils.isNull(user)).thenReturn(false);
        stringUtilsMockedStatic.when(() -> StringUtils.isNull(user.getUser())).thenReturn(false);
        stringUtilsMockedStatic.when(() -> StringUtils.isNotNull(dept)).thenReturn(true);

        SysUserOnline result = service.loginUserToUserOnline(user);
        assertNotNull(result);
        assertEquals("token123", result.getTokenId());
    }

    @Test
    void testLoginUserToUserOnline_DeptNull() {
        LoginUser user = new LoginUser();
        user.setToken("token123");
        user.setIpaddr("127.0.0.1");
        user.setLoginLocation("China");
        user.setBrowser("Chrome");
        user.setOs("Windows");
        user.setLoginTime(System.currentTimeMillis());

        SysUser sysUser = new SysUser();
        sysUser.setDept(null);
        user.setUser(sysUser);

        stringUtilsMockedStatic.when(() -> StringUtils.isNull(user)).thenReturn(false);
        stringUtilsMockedStatic.when(() -> StringUtils.isNull(user.getUser())).thenReturn(false);
        stringUtilsMockedStatic.when(() -> StringUtils.isNotNull(null)).thenReturn(false);

        SysUserOnline result = service.loginUserToUserOnline(user);
        assertNotNull(result);
        assertNull(result.getDeptName());
    }
}