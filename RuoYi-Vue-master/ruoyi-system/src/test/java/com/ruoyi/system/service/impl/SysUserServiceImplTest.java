package com.ruoyi.system.service.impl;

import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.bean.BeanValidators;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.system.domain.SysPost;
import com.ruoyi.system.domain.SysUserPost;
import com.ruoyi.system.domain.SysUserRole;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.system.service.ISysDeptService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.MockedStatic;

import javax.validation.Validator;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SysUserServiceImplTest {

    @InjectMocks
    private SysUserServiceImpl service;

    @Mock
    private SysUserMapper userMapper;
    @Mock
    private SysRoleMapper roleMapper;
    @Mock
    private SysPostMapper postMapper;
    @Mock
    private SysUserRoleMapper userRoleMapper;
    @Mock
    private SysUserPostMapper userPostMapper;
    @Mock
    private ISysConfigService configService;
    @Mock
    private ISysDeptService deptService;
    @Mock
    private Validator validator;

    private MockedStatic<StringUtils> stringUtilsMockedStatic;
    private MockedStatic<SecurityUtils> securityUtilsMockedStatic;
    private MockedStatic<SpringUtils> springUtilsMockedStatic;
    private MockedStatic<SysUser> sysUserMockedStatic;
    private MockedStatic<BeanValidators> beanValidatorsMockedStatic;

    @BeforeEach
    void setUp() {
        stringUtilsMockedStatic = mockStatic(StringUtils.class);
        securityUtilsMockedStatic = mockStatic(SecurityUtils.class);
        springUtilsMockedStatic = mockStatic(SpringUtils.class);
        sysUserMockedStatic = mockStatic(SysUser.class);
        beanValidatorsMockedStatic = mockStatic(BeanValidators.class);
    }

    @AfterEach
    void tearDown() {
        stringUtilsMockedStatic.close();
        securityUtilsMockedStatic.close();
        springUtilsMockedStatic.close();
        sysUserMockedStatic.close();
        beanValidatorsMockedStatic.close();
    }

    @Test
    void selectUserList() {
        SysUser user = new SysUser();
        List<SysUser> expected = Collections.singletonList(user);
        when(userMapper.selectUserList(user)).thenReturn(expected);
        assertEquals(expected, service.selectUserList(user));
    }

    @Test
    void selectAllocatedList() {
        SysUser user = new SysUser();
        List<SysUser> expected = Collections.singletonList(user);
        when(userMapper.selectAllocatedList(user)).thenReturn(expected);
        assertEquals(expected, service.selectAllocatedList(user));
    }

    @Test
    void selectUnallocatedList() {
        SysUser user = new SysUser();
        List<SysUser> expected = Collections.singletonList(user);
        when(userMapper.selectUnallocatedList(user)).thenReturn(expected);
        assertEquals(expected, service.selectUnallocatedList(user));
    }

    @Test
    void selectUserByUserName() {
        SysUser user = new SysUser();
        when(userMapper.selectUserByUserName("admin")).thenReturn(user);
        assertEquals(user, service.selectUserByUserName("admin"));
    }

    @Test
    void selectUserById() {
        SysUser user = new SysUser();
        when(userMapper.selectUserById(1L)).thenReturn(user);
        assertEquals(user, service.selectUserById(1L));
    }

    @Test
    void selectUserRoleGroup_empty() {
        when(roleMapper.selectRolesByUserName("admin")).thenReturn(Collections.emptyList());
        assertEquals("", service.selectUserRoleGroup("admin"));
    }

    @Test
    void selectUserRoleGroup_notEmpty() {
        SysRole role1 = new SysRole(); role1.setRoleName("A");
        SysRole role2 = new SysRole(); role2.setRoleName("B");
        when(roleMapper.selectRolesByUserName("admin")).thenReturn(Arrays.asList(role1, role2));
        assertEquals("A,B", service.selectUserRoleGroup("admin"));
    }

    @Test
    void selectUserPostGroup_empty() {
        when(postMapper.selectPostsByUserName("admin")).thenReturn(Collections.emptyList());
        assertEquals("", service.selectUserPostGroup("admin"));
    }

    @Test
    void selectUserPostGroup_notEmpty() {
        SysPost post1 = new SysPost(); post1.setPostName("P1");
        SysPost post2 = new SysPost(); post2.setPostName("P2");
        when(postMapper.selectPostsByUserName("admin")).thenReturn(Arrays.asList(post1, post2));
        assertEquals("P1,P2", service.selectUserPostGroup("admin"));
    }

    @Test
    void checkUserNameUnique_unique() {
        SysUser user = new SysUser(); user.setUserId(1L); user.setUserName("admin");
        when(userMapper.checkUserNameUnique("admin")).thenReturn(null);
        stringUtilsMockedStatic.when(() -> StringUtils.isNull(any())).thenReturn(false);
        stringUtilsMockedStatic.when(() -> StringUtils.isNotNull(any())).thenReturn(false);
        assertTrue(service.checkUserNameUnique(user));
    }

    @Test
    void checkUserNameUnique_notUnique() {
        SysUser user = new SysUser(); user.setUserId(1L); user.setUserName("admin");
        SysUser info = new SysUser(); info.setUserId(2L);
        when(userMapper.checkUserNameUnique("admin")).thenReturn(info);
        stringUtilsMockedStatic.when(() -> StringUtils.isNull(any())).thenReturn(false);
        stringUtilsMockedStatic.when(() -> StringUtils.isNotNull(any())).thenReturn(true);
        assertFalse(service.checkUserNameUnique(user));
    }

    @Test
    void checkPhoneUnique_unique() {
        SysUser user = new SysUser(); user.setUserId(1L); user.setPhonenumber("123");
        when(userMapper.checkPhoneUnique("123")).thenReturn(null);
        stringUtilsMockedStatic.when(() -> StringUtils.isNull(any())).thenReturn(false);
        stringUtilsMockedStatic.when(() -> StringUtils.isNotNull(any())).thenReturn(false);
        assertTrue(service.checkPhoneUnique(user));
    }

    @Test
    void checkPhoneUnique_notUnique() {
        SysUser user = new SysUser(); user.setUserId(1L); user.setPhonenumber("123");
        SysUser info = new SysUser(); info.setUserId(2L);
        when(userMapper.checkPhoneUnique("123")).thenReturn(info);
        stringUtilsMockedStatic.when(() -> StringUtils.isNull(any())).thenReturn(false);
        stringUtilsMockedStatic.when(() -> StringUtils.isNotNull(any())).thenReturn(true);
        assertFalse(service.checkPhoneUnique(user));
    }

    @Test
    void checkEmailUnique_unique() {
        SysUser user = new SysUser(); user.setUserId(1L); user.setEmail("a@b.com");
        when(userMapper.checkEmailUnique("a@b.com")).thenReturn(null);
        stringUtilsMockedStatic.when(() -> StringUtils.isNull(any())).thenReturn(false);
        stringUtilsMockedStatic.when(() -> StringUtils.isNotNull(any())).thenReturn(false);
        assertTrue(service.checkEmailUnique(user));
    }

    @Test
    void checkEmailUnique_notUnique() {
        SysUser user = new SysUser(); user.setUserId(1L); user.setEmail("a@b.com");
        SysUser info = new SysUser(); info.setUserId(2L);
        when(userMapper.checkEmailUnique("a@b.com")).thenReturn(info);
        stringUtilsMockedStatic.when(() -> StringUtils.isNull(any())).thenReturn(false);
        stringUtilsMockedStatic.when(() -> StringUtils.isNotNull(any())).thenReturn(true);
        assertFalse(service.checkEmailUnique(user));
    }

    @Test
    void checkUserAllowed_admin() {
        SysUser user = mock(SysUser.class);
        when(user.getUserId()).thenReturn(1L);
        when(user.isAdmin()).thenReturn(true);
        stringUtilsMockedStatic.when(() -> StringUtils.isNotNull(1L)).thenReturn(true);
        assertThrows(ServiceException.class, () -> service.checkUserAllowed(user));
    }

    @Test
    void checkUserAllowed_notAdmin() {
        SysUser user = mock(SysUser.class);
        when(user.getUserId()).thenReturn(1L);
        when(user.isAdmin()).thenReturn(false);
        stringUtilsMockedStatic.when(() -> StringUtils.isNotNull(1L)).thenReturn(true);
        assertDoesNotThrow(() -> service.checkUserAllowed(user));
    }

    @Test
    void checkUserDataScope_admin() {
        securityUtilsMockedStatic.when(SecurityUtils::getUserId).thenReturn(1L);
        sysUserMockedStatic.when(() -> SysUser.isAdmin(1L)).thenReturn(true);
        assertDoesNotThrow(() -> service.checkUserDataScope(1L));
    }

    @Test
    void checkUserDataScope_notAdmin_hasUser() {
        securityUtilsMockedStatic.when(SecurityUtils::getUserId).thenReturn(2L);
        sysUserMockedStatic.when(() -> SysUser.isAdmin(2L)).thenReturn(false);
        springUtilsMockedStatic.when(() -> SpringUtils.getAopProxy(any())).thenReturn(service);
        when(userMapper.selectUserList(any())).thenReturn(Collections.singletonList(new SysUser()));
        stringUtilsMockedStatic.when(() -> StringUtils.isEmpty((Collection<?>) any())).thenReturn(false);
        assertDoesNotThrow(() -> service.checkUserDataScope(1L));
    }

    @Test
    void checkUserDataScope_notAdmin_noUser() {
        securityUtilsMockedStatic.when(SecurityUtils::getUserId).thenReturn(2L);
        sysUserMockedStatic.when(() -> SysUser.isAdmin(2L)).thenReturn(false);
        springUtilsMockedStatic.when(() -> SpringUtils.getAopProxy(any())).thenReturn(service);
        when(userMapper.selectUserList(any())).thenReturn(Collections.emptyList());
        stringUtilsMockedStatic.when(() -> StringUtils.isEmpty((Collection<?>) any())).thenReturn(true);
        assertThrows(ServiceException.class, () -> service.checkUserDataScope(1L));
    }




    @Test
    void registerUser_noRole() {
        SysUser user = new SysUser();
        user.setUserName("admin");
        user.setRoleIds(null);
        when(userMapper.insertUser(user)).thenReturn(1);
        stringUtilsMockedStatic.when(() -> StringUtils.isNotNull(null)).thenReturn(false);
        assertTrue(service.registerUser(user));
    }

    @Test
    void registerUser_fail() {
        SysUser user = new SysUser();
        user.setUserName("admin");
        when(userMapper.insertUser(user)).thenReturn(0);
        assertFalse(service.registerUser(user));
    }




    @Test
    void updateUserStatus() {
        SysUser user = new SysUser();
        when(userMapper.updateUser(user)).thenReturn(1);
        assertEquals(1, service.updateUserStatus(user));
    }

    @Test
    void updateUserProfile() {
        SysUser user = new SysUser();
        when(userMapper.updateUser(user)).thenReturn(1);
        assertEquals(1, service.updateUserProfile(user));
    }

    @Test
    void updateUserAvatar_true() {
        when(userMapper.updateUserAvatar("admin", "avatar")).thenReturn(1);
        assertTrue(service.updateUserAvatar("admin", "avatar"));
    }

    @Test
    void updateUserAvatar_false() {
        when(userMapper.updateUserAvatar("admin", "avatar")).thenReturn(0);
        assertFalse(service.updateUserAvatar("admin", "avatar"));
    }

    @Test
    void resetPwd() {
        SysUser user = new SysUser();
        when(userMapper.updateUser(user)).thenReturn(1);
        assertEquals(1, service.resetPwd(user));
    }

    @Test
    void resetUserPwd() {
        when(userMapper.resetUserPwd("admin", "pwd")).thenReturn(1);
        assertEquals(1, service.resetUserPwd("admin", "pwd"));
    }



    @Test
    void insertUserRole_null() {
        SysUser user = new SysUser();
        user.setRoleIds(null);
        stringUtilsMockedStatic.when(() -> StringUtils.isNotNull(null)).thenReturn(false);
        assertDoesNotThrow(() -> service.insertUserRole(user));
    }



    @Test
    void insertUserPost_empty() {
        SysUser user = new SysUser();
        user.setPostIds(null);
        stringUtilsMockedStatic.when(() -> StringUtils.isNotEmpty((Collection<?>) null)).thenReturn(false);
        assertDoesNotThrow(() -> service.insertUserPost(user));
    }



    @Test
    void insertUserRole_userId_empty() {
        stringUtilsMockedStatic.when(() -> StringUtils.isNotEmpty((Collection<?>) null)).thenReturn(false);
        assertDoesNotThrow(() -> service.insertUserRole(1L, null));
    }





    @Test
    void importUser_nullList() {
        stringUtilsMockedStatic.when(() -> StringUtils.isNull(null)).thenReturn(true);
        ServiceException ex = assertThrows(ServiceException.class, () -> service.importUser(null, false, "admin"));
        assertTrue(ex.getMessage().contains("导入用户数据不能为空"));
    }

    @Test
    void importUser_emptyList() {
        stringUtilsMockedStatic.when(() -> StringUtils.isNull(any())).thenReturn(false);
        List<SysUser> list = new ArrayList<>();
        ServiceException ex = assertThrows(ServiceException.class, () -> service.importUser(list, false, "admin"));
        assertTrue(ex.getMessage().contains("导入用户数据不能为空"));
    }





    @Test
    void importUser_existNoUpdate() {
        SysUser user = new SysUser(); user.setUserName("admin");
        List<SysUser> list = Collections.singletonList(user);
        SysUser exist = new SysUser(); exist.setUserId(2L);
        when(userMapper.selectUserByUserName("admin")).thenReturn(exist);

        ServiceException ex = assertThrows(ServiceException.class, () -> service.importUser(list, false, "admin"));
        assertTrue(ex.getMessage().contains("很抱歉，导入失败"));
    }

    @Test
    void importUser_insertException() {
        SysUser user = new SysUser(); user.setUserName("admin"); user.setDeptId(1L);
        List<SysUser> list = Collections.singletonList(user);
        when(userMapper.selectUserByUserName("admin")).thenReturn(null);
        beanValidatorsMockedStatic.when(() -> BeanValidators.validateWithException(any(), any())).thenThrow(new RuntimeException("校验异常"));

        ServiceException ex = assertThrows(ServiceException.class, () -> service.importUser(list, false, "admin"));
        assertTrue(ex.getMessage().contains("很抱歉，导入失败"));
    }
}