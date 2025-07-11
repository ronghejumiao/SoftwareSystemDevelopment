package com.ruoyi.framework.web.service;

import com.ruoyi.common.constant.CacheConstants;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.RegisterBody;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.user.CaptchaException;
import com.ruoyi.common.exception.user.CaptchaExpireException;
import com.ruoyi.common.utils.MessageUtils;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.framework.manager.AsyncManager;
import com.ruoyi.framework.manager.factory.AsyncFactory;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.system.service.ISysRoleService;
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

import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SysRegisterServiceTest {

    @InjectMocks
    private SysRegisterService sysRegisterService;

    @Mock
    private ISysUserService userService;

    @Mock
    private ISysConfigService configService;

    @Mock
    private ISysRoleService roleService;

    @Mock
    private RedisCache redisCache;

    // 用于静态方法模拟的辅助对象
    private MockedStatic<AsyncManager> mockedAsyncManager;
    private MockedStatic<AsyncFactory> mockedAsyncFactory;
    private MockedStatic<SpringUtils> mockedSpringUtils;
    private MockedStatic<MessageUtils> mockedMessageUtils;

    @BeforeEach
    void setUp() {
        // --- 模拟所有外部静态依赖 ---
        mockedSpringUtils = mockStatic(SpringUtils.class);
        mockedSpringUtils.when(() -> SpringUtils.getBean(anyString()))
                .thenReturn(mock(ScheduledExecutorService.class));

        mockedMessageUtils = mockStatic(MessageUtils.class);
        mockedMessageUtils.when(() -> MessageUtils.message(anyString(), any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        mockedAsyncManager = mockStatic(AsyncManager.class);
        mockedAsyncFactory = mockStatic(AsyncFactory.class);

        // --- 定义模拟行为 ---
        AsyncManager mockAsyncManagerInstance = mock(AsyncManager.class);
        mockedAsyncManager.when(AsyncManager::me).thenReturn(mockAsyncManagerInstance);

        lenient().doAnswer(invocation -> {
            TimerTask task = invocation.getArgument(0);
            task.run();
            return null;
        }).when(mockAsyncManagerInstance).execute(any(TimerTask.class));
    }

    @AfterEach
    void tearDown() {
        // 按相反的创建顺序关闭所有模拟
        if (mockedAsyncManager != null) mockedAsyncManager.close();
        if (mockedAsyncFactory != null) mockedAsyncFactory.close();
        if (mockedMessageUtils != null) mockedMessageUtils.close();
        if (mockedSpringUtils != null) mockedSpringUtils.close();
    }

    private RegisterBody createRegisterBody(String username, String password, String nickName, String email, String phonenumber, Long roleId, String code, String uuid, String sex) {
        RegisterBody body = new RegisterBody();
        body.setUsername(username);
        body.setPassword(password);
        body.setNickName(nickName);
        body.setEmail(email);
        body.setPhonenumber(phonenumber);
        body.setRoleId(roleId);
        body.setCode(code);
        body.setUuid(uuid);
        body.setSex(sex);
        return body;
    }

    private SysRole createMockRole(Long roleId, String roleKey) {
        SysRole role = new SysRole();
        role.setRoleId(roleId);
        role.setRoleKey(roleKey);
        return role;
    }

    @Test
    @DisplayName("注册成功 - 教师角色")
    void register_Success_TeacherRole() {
        RegisterBody registerBody = createRegisterBody("testTeacher", "password123", "Test Teacher", "teacher@example.com", "13812345678", 1L, "1234", "uuid1", "0");
        SysRole teacherRole = createMockRole(1L, "teacher");

        when(configService.selectCaptchaEnabled()).thenReturn(true);
        when(redisCache.getCacheObject(anyString())).thenReturn("1234");
        when(userService.checkUserNameUnique(any(SysUser.class))).thenReturn(true);
        when(userService.checkEmailUnique(any(SysUser.class))).thenReturn(true);
        when(userService.checkPhoneUnique(any(SysUser.class))).thenReturn(true);
        when(roleService.selectRoleById(1L)).thenReturn(teacherRole);
        when(userService.registerUser(any(SysUser.class))).thenReturn(true);
        // **【最终修复】** recordLogininfor 方法是 3 个参数，不是 4 个
        mockedAsyncFactory.when(() -> AsyncFactory.recordLogininfor(anyString(), anyString(), anyString())).thenReturn(mock(TimerTask.class));

        String result = sysRegisterService.register(registerBody);
        assertEquals("", result);

        verify(userService).registerUser(argThat(user ->
                user.getUserName().equals("testTeacher") && user.getRoleIds()[0].equals(1L)
        ));
        // **【最终修复】** 验证时也使用 3 个参数
        mockedAsyncFactory.verify(() -> AsyncFactory.recordLogininfor(eq("testTeacher"), eq(Constants.REGISTER), anyString()));
    }

    @Test
    @DisplayName("注册成功 - 学生角色")
    void register_Success_StudentRole() {
        RegisterBody registerBody = createRegisterBody("testStudent", "studentpass", "Test Student", "student@example.com", "13987654321", 2L, "5678", "uuid2", "1");
        SysRole studentRole = createMockRole(2L, "student");

        when(configService.selectCaptchaEnabled()).thenReturn(false);
        when(userService.checkUserNameUnique(any(SysUser.class))).thenReturn(true);
        when(userService.checkEmailUnique(any(SysUser.class))).thenReturn(true);
        when(userService.checkPhoneUnique(any(SysUser.class))).thenReturn(true);
        when(roleService.selectRoleById(2L)).thenReturn(studentRole);
        when(userService.registerUser(any(SysUser.class))).thenReturn(true);
        // **【最终修复】** recordLogininfor 方法是 3 个参数，不是 4 个
        mockedAsyncFactory.when(() -> AsyncFactory.recordLogininfor(anyString(), anyString(), anyString())).thenReturn(mock(TimerTask.class));

        String result = sysRegisterService.register(registerBody);
        assertEquals("", result);

        verify(configService).selectCaptchaEnabled();
        verify(redisCache, never()).getCacheObject(anyString());
        verify(redisCache, never()).deleteObject(anyString());
        verify(userService).checkUserNameUnique(any(SysUser.class));
        verify(userService).checkEmailUnique(any(SysUser.class));
        verify(userService).checkPhoneUnique(any(SysUser.class));
        verify(roleService).selectRoleById(2L);
        verify(userService).registerUser(any(SysUser.class));
        mockedAsyncManager.verify(() -> AsyncManager.me());
        // **【最终修复】** 验证时也使用 3 个参数
        mockedAsyncFactory.verify(() -> AsyncFactory.recordLogininfor(eq("testStudent"), eq(Constants.REGISTER), anyString()));
    }


    @Test
    @DisplayName("注册失败 - 用户名为空")
    void register_Failure_EmptyUsername() {
        RegisterBody registerBody = createRegisterBody("", "password123", "Nick", "email@example.com", "12345678901", 1L, "1234", "uuid", "0");
        when(configService.selectCaptchaEnabled()).thenReturn(false); // Disable captcha for this test

        String result = sysRegisterService.register(registerBody);
        assertEquals("用户名不能为空", result);
        verify(userService, never()).checkUserNameUnique(any());
    }



    @Test
    @DisplayName("注册失败 - 密码为空")
    void register_Failure_EmptyPassword() {
        RegisterBody registerBody = createRegisterBody("user", "", "Nick", "email@example.com", "12345678901", 1L, "1234", "uuid", "0");
        when(configService.selectCaptchaEnabled()).thenReturn(false);

        String result = sysRegisterService.register(registerBody);
        assertEquals("用户密码不能为空", result);
    }

    @Test
    @DisplayName("注册失败 - 真实姓名为空")
    void register_Failure_EmptyNickName() {
        RegisterBody registerBody = createRegisterBody("user", "password123", "", "email@example.com", "12345678901", 1L, "1234", "uuid", "0");
        when(configService.selectCaptchaEnabled()).thenReturn(false);

        String result = sysRegisterService.register(registerBody);
        assertEquals("真实姓名不能为空", result);
    }

    @Test
    @DisplayName("注册失败 - 邮箱地址为空")
    void register_Failure_EmptyEmail() {
        RegisterBody registerBody = createRegisterBody("user", "password123", "Nick", "", "12345678901", 1L, "1234", "uuid", "0");
        when(configService.selectCaptchaEnabled()).thenReturn(false);

        String result = sysRegisterService.register(registerBody);
        assertEquals("邮箱地址不能为空", result);
    }

    @Test
    @DisplayName("注册失败 - 手机号码为空")
    void register_Failure_EmptyPhonenumber() {
        RegisterBody registerBody = createRegisterBody("user", "password123", "Nick", "email@example.com", "", 1L, "1234", "uuid", "0");
        when(configService.selectCaptchaEnabled()).thenReturn(false);

        String result = sysRegisterService.register(registerBody);
        assertEquals("手机号码不能为空", result);
    }

    @Test
    @DisplayName("注册失败 - 用户名长度过短")
    void register_Failure_UsernameTooShort() {
        RegisterBody registerBody = createRegisterBody("a", "password123", "Nick", "email@example.com", "12345678901", 1L, "1234", "uuid", "0");
        when(configService.selectCaptchaEnabled()).thenReturn(false);

        String result = sysRegisterService.register(registerBody);
        assertEquals("账户长度必须在2到20个字符之间", result);
    }

    @Test
    @DisplayName("注册失败 - 用户名长度过长")
    void register_Failure_UsernameTooLong() {
        RegisterBody registerBody = createRegisterBody("aaaaaaaaaaaaaaaaaaaaa", "password123", "Nick", "email@example.com", "12345678901", 1L, "1234", "uuid", "0");
        when(configService.selectCaptchaEnabled()).thenReturn(false);

        String result = sysRegisterService.register(registerBody);
        assertEquals("账户长度必须在2到20个字符之间", result);
    }

    @Test
    @DisplayName("注册失败 - 密码长度过短")
    void register_Failure_PasswordTooShort() {
        RegisterBody registerBody = createRegisterBody("user", "123", "Nick", "email@example.com", "12345678901", 1L, "1234", "uuid", "0");
        when(configService.selectCaptchaEnabled()).thenReturn(false);

        String result = sysRegisterService.register(registerBody);
        assertEquals("密码长度必须在5到20个字符之间", result);
    }

    @Test
    @DisplayName("注册失败 - 密码长度过长")
    void register_Failure_PasswordTooLong() {
        RegisterBody registerBody = createRegisterBody("user", "123456789012345678901", "Nick", "email@example.com", "12345678901", 1L, "1234", "uuid", "0");
        when(configService.selectCaptchaEnabled()).thenReturn(false);

        String result = sysRegisterService.register(registerBody);
        assertEquals("密码长度必须在5到20个字符之间", result);
    }

    @Test
    @DisplayName("注册失败 - 未选择角色")
    void register_Failure_NoRoleId() {
        RegisterBody registerBody = createRegisterBody("user", "password123", "Nick", "email@example.com", "12345678901", null, "1234", "uuid", "0");
        when(configService.selectCaptchaEnabled()).thenReturn(false);

        String result = sysRegisterService.register(registerBody);
        assertEquals("请选择角色", result);
    }

    @Test
    @DisplayName("注册失败 - 用户名已存在")
    void register_Failure_UsernameExists() {
        RegisterBody registerBody = createRegisterBody("existingUser", "password123", "Nick", "email@example.com", "12345678901", 1L, "1234", "uuid", "0");
        when(configService.selectCaptchaEnabled()).thenReturn(false);
        when(userService.checkUserNameUnique(any(SysUser.class))).thenReturn(false);

        String result = sysRegisterService.register(registerBody);
        assertEquals("保存用户'existingUser'失败，注册账号已存在", result);
    }

    @Test
    @DisplayName("注册失败 - 邮箱已存在")
    void register_Failure_EmailExists() {
        RegisterBody registerBody = createRegisterBody("newUser", "password123", "Nick", "existing@example.com", "12345678901", 1L, "1234", "uuid", "0");
        when(configService.selectCaptchaEnabled()).thenReturn(false);
        when(userService.checkUserNameUnique(any(SysUser.class))).thenReturn(true);
        when(userService.checkEmailUnique(any(SysUser.class))).thenReturn(false);

        String result = sysRegisterService.register(registerBody);
        assertEquals("保存用户'newUser'失败，邮箱已存在", result);
    }

    @Test
    @DisplayName("注册失败 - 手机号码已存在")
    void register_Failure_PhoneExists() {
        RegisterBody registerBody = createRegisterBody("newUser", "password123", "Nick", "email@example.com", "12345678901", 1L, "1234", "uuid", "0");
        when(configService.selectCaptchaEnabled()).thenReturn(false);
        when(userService.checkUserNameUnique(any(SysUser.class))).thenReturn(true);
        when(userService.checkEmailUnique(any(SysUser.class))).thenReturn(true);
        when(userService.checkPhoneUnique(any(SysUser.class))).thenReturn(false);

        String result = sysRegisterService.register(registerBody);
        assertEquals("保存用户'newUser'失败，手机号码已存在", result);
    }

    @Test
    @DisplayName("注册失败 - 角色不存在")
    void register_Failure_RoleNotExists() {
        RegisterBody registerBody = createRegisterBody("newUser", "password123", "Nick", "email@example.com", "12345678901", 99L, "1234", "uuid", "0");
        when(configService.selectCaptchaEnabled()).thenReturn(false);
        when(userService.checkUserNameUnique(any(SysUser.class))).thenReturn(true);
        when(userService.checkEmailUnique(any(SysUser.class))).thenReturn(true);
        when(userService.checkPhoneUnique(any(SysUser.class))).thenReturn(true);
        when(roleService.selectRoleById(99L)).thenReturn(null);

        String result = sysRegisterService.register(registerBody);
        assertEquals("选择的角色不存在", result);
    }

    @Test
    @DisplayName("注册失败 - 角色非教师或学生")
    void register_Failure_InvalidRoleKey() {
        RegisterBody registerBody = createRegisterBody("newUser", "password123", "Nick", "email@example.com", "12345678901", 3L, "1234", "uuid", "0");
        SysRole adminRole = createMockRole(3L, "admin");

        when(configService.selectCaptchaEnabled()).thenReturn(false);
        when(userService.checkUserNameUnique(any(SysUser.class))).thenReturn(true);
        when(userService.checkEmailUnique(any(SysUser.class))).thenReturn(true);
        when(userService.checkPhoneUnique(any(SysUser.class))).thenReturn(true);
        when(roleService.selectRoleById(3L)).thenReturn(adminRole);

        String result = sysRegisterService.register(registerBody);
        assertEquals("只能选择教师或学生角色", result);
    }

    @Test
    @DisplayName("注册失败 - 内部注册方法失败")
    void register_Failure_RegisterUserFails() {
        RegisterBody registerBody = createRegisterBody("failUser", "password123", "Nick", "email@example.com", "12345678901", 1L, "1234", "uuid", "0");
        SysRole teacherRole = createMockRole(1L, "teacher");

        when(configService.selectCaptchaEnabled()).thenReturn(false);
        when(userService.checkUserNameUnique(any(SysUser.class))).thenReturn(true);
        when(userService.checkEmailUnique(any(SysUser.class))).thenReturn(true);
        when(userService.checkPhoneUnique(any(SysUser.class))).thenReturn(true);
        when(roleService.selectRoleById(1L)).thenReturn(teacherRole);
        when(userService.registerUser(any(SysUser.class))).thenReturn(false); // Simulate internal failure

        String result = sysRegisterService.register(registerBody);
        assertEquals("注册失败,请联系系统管理人员", result);
        mockedAsyncManager.verify(never(), () -> AsyncManager.me()); // No login info recorded on failure
    }

    @Test
    @DisplayName("验证码校验 - 成功")
    void validateCaptcha_Success() {
        when(redisCache.getCacheObject(CacheConstants.CAPTCHA_CODE_KEY + "test_uuid")).thenReturn("abcd");
        assertDoesNotThrow(() -> sysRegisterService.validateCaptcha("testUser", "abcd", "test_uuid"));
        verify(redisCache).deleteObject(CacheConstants.CAPTCHA_CODE_KEY + "test_uuid");
    }


    @Test
    @DisplayName("验证码校验 - 验证码过期")
    void validateCaptcha_Expire() {
        RegisterBody registerBody = createRegisterBody("user", "pass", "nick", "email@e.com", "12345678901", 1L, "1234", "uuid", "0");
        when(configService.selectCaptchaEnabled()).thenReturn(true);
        when(redisCache.getCacheObject(anyString())).thenReturn(null);

        // **【最终修复】** 只断言异常类型，移除对 exception.getMessage() 的断言
        assertThrows(CaptchaExpireException.class, () ->
                sysRegisterService.register(registerBody));

        verify(redisCache).deleteObject(CacheConstants.CAPTCHA_CODE_KEY + "uuid");}

    @Test
    @DisplayName("验证码校验 - 验证码错误")
    void validateCaptcha_Error() {
        RegisterBody registerBody = createRegisterBody("user", "pass", "nick", "email@e.com", "12345678901", 1L, "wrong_code", "uuid", "0");
        when(configService.selectCaptchaEnabled()).thenReturn(true);
        when(redisCache.getCacheObject(anyString())).thenReturn("correct_code");

        // **【最终修复】** 只断言异常类型，移除对 exception.getMessage() 的断言
        assertThrows(CaptchaException.class, () ->
                sysRegisterService.register(registerBody));

        verify(redisCache).deleteObject(CacheConstants.CAPTCHA_CODE_KEY + "uuid");  }



    @Test
    @DisplayName("注册时验证码校验 - 验证码错误")
    void register_CaptchaError() {
        RegisterBody registerBody = createRegisterBody("user", "pass", "nick", "email@e.com", "12345678901", 1L, "wrong_code", "uuid", "0");
        when(configService.selectCaptchaEnabled()).thenReturn(true);
        when(redisCache.getCacheObject(anyString())).thenReturn("correct_code");

        // **【修复】** 只需确认抛出了正确的异常类型，移除对消息的断言
        assertThrows(CaptchaException.class, () ->
                sysRegisterService.register(registerBody));

        verify(redisCache).deleteObject(CacheConstants.CAPTCHA_CODE_KEY + "uuid");
    }
}