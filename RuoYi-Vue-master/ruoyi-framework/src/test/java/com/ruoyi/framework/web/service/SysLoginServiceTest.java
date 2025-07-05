package com.ruoyi.framework.web.service;

import com.ruoyi.common.constant.CacheConstants;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.exception.user.BlackListException;
import com.ruoyi.common.exception.user.CaptchaException;
import com.ruoyi.common.exception.user.CaptchaExpireException;
import com.ruoyi.common.exception.user.UserNotExistsException;
import com.ruoyi.common.exception.user.UserPasswordNotMatchException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.MessageUtils;
import com.ruoyi.common.utils.ip.IpUtils;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.framework.manager.AsyncManager;
import com.ruoyi.framework.manager.factory.AsyncFactory;
import com.ruoyi.framework.security.context.AuthenticationContextHolder;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.system.service.ISysUserService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Date;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * 针对 SysLoginService 的单元测试类 (高覆盖率版)
 *
 * @author Gemini
 */
@ExtendWith(MockitoExtension.class)
class SysLoginServiceTest {

    @InjectMocks
    private SysLoginService sysLoginService;

    // --- 内部依赖 ---
    @Mock
    private TokenService tokenService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private RedisCache redisCache;
    @Mock
    private ISysUserService userService;
    @Mock
    private ISysConfigService configService;

    // --- 静态类模拟 ---
    private MockedStatic<SpringUtils> springUtilsMockedStatic;
    private MockedStatic<AuthenticationContextHolder> authenticationContextHolderMockedStatic;
    private MockedStatic<AsyncManager> asyncManagerMockedStatic;
    private MockedStatic<AsyncFactory> asyncFactoryMockedStatic;
    private MockedStatic<MessageUtils> messageUtilsMockedStatic;
    private MockedStatic<IpUtils> ipUtilsMockedStatic;
    private MockedStatic<DateUtils> dateUtilsMockedStatic;

    @BeforeEach
    void setUp() {
        // 模拟所有静态工具类的行为
        springUtilsMockedStatic = mockStatic(SpringUtils.class);
        authenticationContextHolderMockedStatic = mockStatic(AuthenticationContextHolder.class);
        asyncManagerMockedStatic = mockStatic(AsyncManager.class);
        asyncFactoryMockedStatic = mockStatic(AsyncFactory.class);
        messageUtilsMockedStatic = mockStatic(MessageUtils.class);
        ipUtilsMockedStatic = mockStatic(IpUtils.class);
        dateUtilsMockedStatic = mockStatic(DateUtils.class);

        // 设置默认的模拟行为
        springUtilsMockedStatic.when(() -> SpringUtils.getBean(any(Class.class))).thenReturn(mock(ScheduledExecutorService.class));
        springUtilsMockedStatic.when(() -> SpringUtils.getBean(anyString())).thenReturn(mock(ScheduledExecutorService.class));
        AsyncManager mockAsyncManager = mock(AsyncManager.class);
        when(AsyncManager.me()).thenReturn(mockAsyncManager);
        lenient().doNothing().when(mockAsyncManager).execute(any(TimerTask.class));
        lenient().when(AsyncFactory.recordLogininfor(anyString(), anyString(), anyString())).thenReturn(mock(TimerTask.class));
        when(MessageUtils.message(anyString(), any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(IpUtils.getIpAddr()).thenReturn("127.0.0.1");
    }

    @AfterEach
    void tearDown() {
        // 关闭所有静态模拟
        springUtilsMockedStatic.close();
        authenticationContextHolderMockedStatic.close();
        asyncManagerMockedStatic.close();
        asyncFactoryMockedStatic.close();
        messageUtilsMockedStatic.close();
        ipUtilsMockedStatic.close();
        dateUtilsMockedStatic.close();
    }

    @Test
    @DisplayName("登录成功应返回Token (验证码开启)")
    void login_Success_WithCaptchaEnabled_ShouldReturnToken() {
        // Arrange
        String username = "admin";
        String password = "password123";
        String code = "1234";
        String uuid = "uuid1234";
        String fakeToken = "fake-jwt-token";

        when(configService.selectCaptchaEnabled()).thenReturn(true);
        when(redisCache.getCacheObject(anyString())).thenReturn(code);
        when(configService.selectConfigByKey(eq("sys.login.blackIPList"))).thenReturn("");

        Authentication mockAuthentication = mock(Authentication.class);
        LoginUser mockLoginUser = new LoginUser(1L, 1L, new SysUser(), null);
        when(mockAuthentication.getPrincipal()).thenReturn(mockLoginUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mockAuthentication);
        when(tokenService.createToken(any(LoginUser.class))).thenReturn(fakeToken);

        // Act
        String resultToken = sysLoginService.login(username, password, code, uuid);

        // Assert
        assertNotNull(resultToken);
        assertEquals(fakeToken, resultToken);
        verify(redisCache).deleteObject(CacheConstants.CAPTCHA_CODE_KEY + uuid);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenService).createToken(mockLoginUser);
        verify(userService).updateUserProfile(any(SysUser.class));
        asyncFactoryMockedStatic.verify(() -> AsyncFactory.recordLogininfor(eq(username), eq(Constants.LOGIN_SUCCESS), anyString()));
    }

    // 【新增测试用例】 用于提高行覆盖率
    @Test
    @DisplayName("登录成功 (验证码关闭) 应跳过验证码校验")
    void login_Success_WithCaptchaDisabled_ShouldSkipCaptchaValidation() {
        // Arrange
        String username = "admin";
        String password = "password123";
        String fakeToken = "fake-jwt-token-no-captcha";

        // 核心测试点：禁用验证码
        when(configService.selectCaptchaEnabled()).thenReturn(false);

        // 其他成功路径的模拟
        when(configService.selectConfigByKey(eq("sys.login.blackIPList"))).thenReturn("");
        Authentication mockAuthentication = mock(Authentication.class);
        LoginUser mockLoginUser = new LoginUser(1L, 1L, new SysUser(), null);
        when(mockAuthentication.getPrincipal()).thenReturn(mockLoginUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mockAuthentication);
        when(tokenService.createToken(any(LoginUser.class))).thenReturn(fakeToken);

        // Act
        String resultToken = sysLoginService.login(username, password, null, null); // 验证码和uuid为null

        // Assert
        assertEquals(fakeToken, resultToken);
        // 验证RedisCache的方法【从未】被调用，证明跳过了if分支
        verify(redisCache, never()).getCacheObject(anyString());
        verify(redisCache, never()).deleteObject(anyString());
    }


    @Test
    @DisplayName("登录失败：验证码已过期")
    void login_Failure_WhenCaptchaExpired() {
        when(configService.selectCaptchaEnabled()).thenReturn(true);
        when(redisCache.getCacheObject(anyString())).thenReturn(null);
        assertThrows(CaptchaExpireException.class, () -> sysLoginService.login("user", "pass", "1234", "uuid"));
        asyncFactoryMockedStatic.verify(() -> AsyncFactory.recordLogininfor(anyString(), eq(Constants.LOGIN_FAIL), eq("user.jcaptcha.expire")));
    }

    @Test
    @DisplayName("登录失败：验证码错误")
    void login_Failure_WhenCaptchaIsIncorrect() {
        when(configService.selectCaptchaEnabled()).thenReturn(true);
        when(redisCache.getCacheObject(anyString())).thenReturn("correct-code");
        assertThrows(CaptchaException.class, () -> sysLoginService.login("user", "pass", "wrong-code", "uuid"));
        asyncFactoryMockedStatic.verify(() -> AsyncFactory.recordLogininfor(anyString(), eq(Constants.LOGIN_FAIL), eq("user.jcaptcha.error")));
    }

    @Test
    @DisplayName("登录失败：用户名或密码为空")
    void login_Failure_WhenUsernameOrPasswordIsEmpty() {
        assertThrows(UserNotExistsException.class, () -> sysLoginService.login("", "password", "", ""));
        asyncFactoryMockedStatic.verify(() -> AsyncFactory.recordLogininfor(eq(""), eq(Constants.LOGIN_FAIL), eq("not.null")));
    }

    // 【新增测试用例】 用于提高行覆盖率
    @Test
    @DisplayName("登录失败：密码过短")
    void login_Failure_WhenPasswordIsTooShort() {
        // 假设 UserConstants.PASSWORD_MIN_LENGTH > 4
        String shortPassword = "1234";
        when(configService.selectCaptchaEnabled()).thenReturn(false);

        assertThrows(UserPasswordNotMatchException.class, () -> {
            sysLoginService.login("user", shortPassword, "", "");
        });
        asyncFactoryMockedStatic.verify(() -> AsyncFactory.recordLogininfor(anyString(), eq(Constants.LOGIN_FAIL), eq("user.password.not.match")));
    }



    @Test
    @DisplayName("登录失败：密码错误 (BadCredentialsException)")
    void login_Failure_WhenPasswordIsWrong() {
        when(configService.selectCaptchaEnabled()).thenReturn(false);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("密码错误"));
        assertThrows(UserPasswordNotMatchException.class, () -> sysLoginService.login("user", "wrong-password", "", ""));
        asyncFactoryMockedStatic.verify(() -> AsyncFactory.recordLogininfor(anyString(), eq(Constants.LOGIN_FAIL), eq("user.password.not.match")));
    }


    @Test
    @DisplayName("记录登录信息应正确更新用户信息")
    void recordLoginInfo_ShouldUpdateUserProfileWithCorrectInfo() {
        long userId = 1L;
        String testIp = "127.0.0.1";
        Date testDate = new Date();
        when(IpUtils.getIpAddr()).thenReturn(testIp);
        dateUtilsMockedStatic.when(DateUtils::getNowDate).thenReturn(testDate);
        ArgumentCaptor<SysUser> userCaptor = ArgumentCaptor.forClass(SysUser.class);

        sysLoginService.recordLoginInfo(userId);

        verify(userService, times(1)).updateUserProfile(userCaptor.capture());
        SysUser capturedUser = userCaptor.getValue();
        assertEquals(userId, capturedUser.getUserId());
        assertEquals(testIp, capturedUser.getLoginIp());
        assertEquals(testDate, capturedUser.getLoginDate());
    }
}