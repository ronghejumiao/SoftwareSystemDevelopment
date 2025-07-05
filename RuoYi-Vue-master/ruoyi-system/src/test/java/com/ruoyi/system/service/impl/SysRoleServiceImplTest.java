package com.ruoyi.system.service.impl;

import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.system.domain.SysRoleDept;
import com.ruoyi.system.domain.SysRoleMenu;
import com.ruoyi.system.domain.SysUserRole;
import com.ruoyi.system.mapper.SysRoleDeptMapper;
import com.ruoyi.system.mapper.SysRoleMapper;
import com.ruoyi.system.mapper.SysRoleMenuMapper;
import com.ruoyi.system.mapper.SysUserRoleMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.MockedStatic;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SysRoleServiceImplTest {

    @InjectMocks
    private SysRoleServiceImpl sysRoleService;

    @Mock
    private SysRoleMapper roleMapper;
    @Mock
    private SysRoleMenuMapper roleMenuMapper;
    @Mock
    private SysUserRoleMapper userRoleMapper;
    @Mock
    private SysRoleDeptMapper roleDeptMapper;

    private MockedStatic<SpringUtils> springUtilsMockedStatic;
    private MockedStatic<SecurityUtils> securityUtilsMockedStatic;
    private MockedStatic<StringUtils> stringUtilsMockedStatic;
    private MockedStatic<SysUser> sysUserMockedStatic;

    @BeforeEach
    void setUp() {
        springUtilsMockedStatic = mockStatic(SpringUtils.class);
        securityUtilsMockedStatic = mockStatic(SecurityUtils.class);
        stringUtilsMockedStatic = mockStatic(StringUtils.class);
        sysUserMockedStatic = mockStatic(SysUser.class);
    }

    @AfterEach
    void tearDown() {
        springUtilsMockedStatic.close();
        securityUtilsMockedStatic.close();
        stringUtilsMockedStatic.close();
        sysUserMockedStatic.close();
    }

    @Test
    void testSelectRoleList() {
        SysRole role = new SysRole();
        List<SysRole> expected = Collections.singletonList(role);
        when(roleMapper.selectRoleList(role)).thenReturn(expected);
        assertEquals(expected, sysRoleService.selectRoleList(role));
    }

    @Test
    void testSelectPublicRoleList() {
        SysRole role = new SysRole();
        List<SysRole> expected = Collections.singletonList(role);
        when(roleMapper.selectRoleList(role)).thenReturn(expected);
        assertEquals(expected, sysRoleService.selectPublicRoleList(role));
    }

    @Test
    void testSelectRolesByUserId() {
        SysRole role1 = new SysRole(); role1.setRoleId(1L);
        SysRole role2 = new SysRole(); role2.setRoleId(2L);
        SysRole userRole = new SysRole(); userRole.setRoleId(1L);
        List<SysRole> userRoles = Collections.singletonList(userRole);
        List<SysRole> allRoles = Arrays.asList(role1, role2);

        when(roleMapper.selectRolePermissionByUserId(1L)).thenReturn(userRoles);
        springUtilsMockedStatic.when(() -> SpringUtils.getAopProxy(any())).thenReturn(sysRoleService);
        when(roleMapper.selectRoleList(any())).thenReturn(allRoles);

        List<SysRole> result = sysRoleService.selectRolesByUserId(1L);
        assertTrue(result.get(0).isFlag());
        assertFalse(result.get(1).isFlag());
    }

    @Test
    void testSelectRolePermissionByUserId() {
        SysRole role = new SysRole(); role.setRoleKey("admin,user");
        List<SysRole> perms = Collections.singletonList(role);
        when(roleMapper.selectRolePermissionByUserId(1L)).thenReturn(perms);
        stringUtilsMockedStatic.when(() -> StringUtils.isNotNull(any())).thenReturn(true);

        Set<String> result = sysRoleService.selectRolePermissionByUserId(1L);
        assertTrue(result.contains("admin"));
        assertTrue(result.contains("user"));
    }

    @Test
    void testSelectRoleAll() {
        springUtilsMockedStatic.when(() -> SpringUtils.getAopProxy(any())).thenReturn(sysRoleService);
        when(roleMapper.selectRoleList(any())).thenReturn(Collections.emptyList());
        assertNotNull(sysRoleService.selectRoleAll());
    }

    @Test
    void testSelectRoleListByUserId() {
        List<Long> expected = Arrays.asList(1L, 2L);
        when(roleMapper.selectRoleListByUserId(1L)).thenReturn(expected);
        assertEquals(expected, sysRoleService.selectRoleListByUserId(1L));
    }

    @Test
    void testSelectRoleById() {
        SysRole role = new SysRole();
        when(roleMapper.selectRoleById(1L)).thenReturn(role);
        assertEquals(role, sysRoleService.selectRoleById(1L));
    }

    @Test
    void testCheckRoleNameUnique_Unique() {
        SysRole role = new SysRole(); role.setRoleId(1L); role.setRoleName("test");
        when(roleMapper.checkRoleNameUnique("test")).thenReturn(null);
        stringUtilsMockedStatic.when(() -> StringUtils.isNull(any())).thenReturn(false);
        stringUtilsMockedStatic.when(() -> StringUtils.isNotNull(any())).thenReturn(false);
        assertTrue(sysRoleService.checkRoleNameUnique(role));
    }

    @Test
    void testCheckRoleNameUnique_NotUnique() {
        SysRole role = new SysRole(); role.setRoleId(1L); role.setRoleName("test");
        SysRole info = new SysRole(); info.setRoleId(2L);
        when(roleMapper.checkRoleNameUnique("test")).thenReturn(info);
        stringUtilsMockedStatic.when(() -> StringUtils.isNull(any())).thenReturn(false);
        stringUtilsMockedStatic.when(() -> StringUtils.isNotNull(any())).thenReturn(true);
        assertFalse(sysRoleService.checkRoleNameUnique(role));
    }

    @Test
    void testCheckRoleKeyUnique_Unique() {
        SysRole role = new SysRole(); role.setRoleId(1L); role.setRoleKey("test");
        when(roleMapper.checkRoleKeyUnique("test")).thenReturn(null);
        stringUtilsMockedStatic.when(() -> StringUtils.isNull(any())).thenReturn(false);
        stringUtilsMockedStatic.when(() -> StringUtils.isNotNull(any())).thenReturn(false);
        assertTrue(sysRoleService.checkRoleKeyUnique(role));
    }

    @Test
    void testCheckRoleKeyUnique_NotUnique() {
        SysRole role = new SysRole(); role.setRoleId(1L); role.setRoleKey("test");
        SysRole info = new SysRole(); info.setRoleId(2L);
        when(roleMapper.checkRoleKeyUnique("test")).thenReturn(info);
        stringUtilsMockedStatic.when(() -> StringUtils.isNull(any())).thenReturn(false);
        stringUtilsMockedStatic.when(() -> StringUtils.isNotNull(any())).thenReturn(true);
        assertFalse(sysRoleService.checkRoleKeyUnique(role));
    }

    @Test
    void testCheckRoleAllowed_Admin() {
        SysRole role = new SysRole(); role.setRoleId(1L);
        when(role.isAdmin()).thenReturn(true);
        stringUtilsMockedStatic.when(() -> StringUtils.isNotNull(any())).thenReturn(true);
        assertThrows(ServiceException.class, () -> sysRoleService.checkRoleAllowed(role));
    }


    @Test
    void testCheckRoleDataScope_Admin() {
        securityUtilsMockedStatic.when(SecurityUtils::getUserId).thenReturn(1L);
        sysUserMockedStatic.when(() -> SysUser.isAdmin(1L)).thenReturn(true);
        assertDoesNotThrow(() -> sysRoleService.checkRoleDataScope(1L));
    }

    @Test
    void testCheckRoleDataScope_NotAdmin_HasRole() {
        securityUtilsMockedStatic.when(SecurityUtils::getUserId).thenReturn(2L);
        sysUserMockedStatic.when(() -> SysUser.isAdmin(2L)).thenReturn(false);
        springUtilsMockedStatic.when(() -> SpringUtils.getAopProxy(any())).thenReturn(sysRoleService);
        when(roleMapper.selectRoleList(any())).thenReturn(Collections.singletonList(new SysRole()));
        assertDoesNotThrow(() -> sysRoleService.checkRoleDataScope(1L));
    }



    @Test
    void testCountUserRoleByRoleId() {
        when(userRoleMapper.countUserRoleByRoleId(1L)).thenReturn(2);
        assertEquals(2, sysRoleService.countUserRoleByRoleId(1L));
    }

    @Test
    void testInsertRole() {
        SysRole role = new SysRole(); role.setMenuIds(new Long[]{1L, 2L}); role.setRoleId(1L);
        when(roleMapper.insertRole(role)).thenReturn(1);
        when(roleMenuMapper.batchRoleMenu(anyList())).thenReturn(2);
        assertEquals(2, sysRoleService.insertRole(role));
    }

    @Test
    void testUpdateRole() {
        SysRole role = new SysRole(); role.setMenuIds(new Long[]{1L, 2L}); role.setRoleId(1L);
        when(roleMapper.updateRole(role)).thenReturn(1);
        when(roleMenuMapper.deleteRoleMenuByRoleId(1L)).thenReturn(1);
        when(roleMenuMapper.batchRoleMenu(anyList())).thenReturn(2);
        assertEquals(2, sysRoleService.updateRole(role));
    }

    @Test
    void testUpdateRoleStatus() {
        SysRole role = new SysRole();
        when(roleMapper.updateRole(role)).thenReturn(1);
        assertEquals(1, sysRoleService.updateRoleStatus(role));
    }

    @Test
    void testAuthDataScope() {
        SysRole role = new SysRole(); role.setDeptIds(new Long[]{1L, 2L}); role.setRoleId(1L);
        when(roleMapper.updateRole(role)).thenReturn(1);
        when(roleDeptMapper.deleteRoleDeptByRoleId(1L)).thenReturn(1);
        when(roleDeptMapper.batchRoleDept(anyList())).thenReturn(2);
        assertEquals(2, sysRoleService.authDataScope(role));
    }

    @Test
    void testInsertRoleMenu_Empty() {
        SysRole role = new SysRole(); role.setMenuIds(new Long[]{});
        assertEquals(1, sysRoleService.insertRoleMenu(role));
    }

    @Test
    void testInsertRoleMenu_NotEmpty() {
        SysRole role = new SysRole(); role.setMenuIds(new Long[]{1L, 2L}); role.setRoleId(1L);
        when(roleMenuMapper.batchRoleMenu(anyList())).thenReturn(2);
        assertEquals(2, sysRoleService.insertRoleMenu(role));
    }

    @Test
    void testInsertRoleDept_Empty() {
        SysRole role = new SysRole(); role.setDeptIds(new Long[]{});
        assertEquals(1, sysRoleService.insertRoleDept(role));
    }

    @Test
    void testInsertRoleDept_NotEmpty() {
        SysRole role = new SysRole(); role.setDeptIds(new Long[]{1L, 2L}); role.setRoleId(1L);
        when(roleDeptMapper.batchRoleDept(anyList())).thenReturn(2);
        assertEquals(2, sysRoleService.insertRoleDept(role));
    }

    @Test
    void testDeleteRoleById() {
        when(roleMenuMapper.deleteRoleMenuByRoleId(1L)).thenReturn(1);
        when(roleDeptMapper.deleteRoleDeptByRoleId(1L)).thenReturn(1);
        when(roleMapper.deleteRoleById(1L)).thenReturn(1);
        assertEquals(1, sysRoleService.deleteRoleById(1L));
    }





    @Test
    void testDeleteAuthUser() {
        SysUserRole userRole = new SysUserRole();
        when(userRoleMapper.deleteUserRoleInfo(userRole)).thenReturn(1);
        assertEquals(1, sysRoleService.deleteAuthUser(userRole));
    }

    @Test
    void testDeleteAuthUsers() {
        when(userRoleMapper.deleteUserRoleInfos(1L, new Long[]{2L})).thenReturn(1);
        assertEquals(1, sysRoleService.deleteAuthUsers(1L, new Long[]{2L}));
    }

    @Test
    void testInsertAuthUsers() {
        when(userRoleMapper.batchUserRole(anyList())).thenReturn(2);
        assertEquals(2, sysRoleService.insertAuthUsers(1L, new Long[]{2L, 3L}));
    }
}