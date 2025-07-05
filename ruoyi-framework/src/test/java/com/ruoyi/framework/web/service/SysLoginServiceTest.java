package com.ruoyi.framework.web.service;

import com.ruoyi.common.constant.CacheConstants;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.exception.user.*;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.MessageUtils;
import com.ruoyi.common.utils.ip.IpUtils;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("登录服务测试")
class SysLoginServiceTest {

    @InjectMocks
    private SysLoginService sysLoginService;

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
    
    private MockedStatic<AuthenticationContextHolder> mockedAuthContext;
    private MockedStatic<AsyncManager> mockedAsyncManager;
    private MockedStatic<IpUtils> mockedIpUtils;
    private MockedStatic<DateUtils> mockedDateUtils;
    private MockedStatic<MessageUtils> mockedMessageUtils;

    @BeforeEach
    void setUp() {
        // Mock static methods
        mockedAuthContext = mockStatic(AuthenticationContextHolder.class);
        mockedAsyncManager = mockStatic(AsyncManager.class);
        // Mock the singleton instance and its executor
        AsyncManager asyncManagerInstance = mock(AsyncManager.class);
        when(AsyncManager.me()).thenReturn(asyncManagerInstance);
        doNothing().when(asyncManagerInstance).execute(any());

        mockedIpUtils = mockStatic(IpUtils.class);
        mockedDateUtils = mockStatic(DateUtils.class);
        mockedMessageUtils = mockStatic(MessageUtils.class);
    }

    @AfterEach
    void tearDown() {
        mockedAuthContext.close();
        mockedAsyncManager.close();
        mockedIpUtils.close();
        mockedDateUtils.close();
        mockedMessageUtils.close();
    }
    
    // --- login tests ---
    
    @Test
    @DisplayName("登录成功")
    void login_Success() {
        String username = "testuser";
        String password = "password";
        String token = "mock-token";
        LoginUser loginUser = new LoginUser(1L, 1L, new SysUser(), Collections.emptySet());
        Authentication auth = mock(Authentication.class);

        when(configService.selectCaptchaEnabled()).thenReturn(false);
        when(configService.selectConfigByKey(anyString())).thenReturn("127.0.0.1"); // Blacklist does not match
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(loginUser);
        when(tokenService.createToken(any(LoginUser.class))).thenReturn(token);

        String result = sysLoginService.login(username, password, "","");

        assertEquals(token, result);
        verify(userService).updateUserProfile(any(SysUser.class));
    }
    
    @Test
    @DisplayName("登录失败 - 密码错误")
    void login_BadCredentials() {
        when(configService.selectCaptchaEnabled()).thenReturn(false);
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));
        
        assertThrows(UserPasswordNotMatchException.class, () -> 
            sysLoginService.login("user", "wrong_password", "", "")
        );
    }
    
    @Test
    @DisplayName("登录失败 - 其他认证异常")
    void login_OtherAuthenticationException() {
        when(configService.selectCaptchaEnabled()).thenReturn(false);
        when(authenticationManager.authenticate(any())).thenThrow(new ServiceException("Some other error"));

        assertThrows(ServiceException.class, () -> 
            sysLoginService.login("user", "password", "", "")
        );
    }
    
    // --- validateCaptcha tests ---

    @Test
    @DisplayName("验证码校验 - 已禁用")
    void validateCaptcha_Disabled() {
        when(configService.selectCaptchaEnabled()).thenReturn(false);
        // No exception should be thrown
        sysLoginService.validateCaptcha("user", "code", "uuid");
        verify(redisCache, never()).getCacheObject(anyString());
    }
    
    @Test
    @DisplayName("验证码校验 - 成功")
    void validateCaptcha_Success() {
        when(configService.selectCaptchaEnabled()).thenReturn(true);
        when(redisCache.getCacheObject(anyString())).thenReturn("captcha");
        
        sysLoginService.validateCaptcha("user", "captcha", "uuid");

        verify(redisCache).deleteObject(anyString());
    }

    @Test
    @DisplayName("验证码校验 - 已过期")
    void validateCaptcha_Expired() {
        when(configService.selectCaptchaEnabled()).thenReturn(true);
        when(redisCache.getCacheObject(anyString())).thenReturn(null);

        assertThrows(CaptchaExpireException.class, () ->
            sysLoginService.validateCaptcha("user", "code", "uuid")
        );
    }
    
    @Test
    @DisplayName("验证码校验 - 不正确")
    void validateCaptcha_Incorrect() {
        when(configService.selectCaptchaEnabled()).thenReturn(true);
        when(redisCache.getCacheObject(anyString())).thenReturn("correct_code");

        assertThrows(CaptchaException.class, () ->
            sysLoginService.validateCaptcha("user", "wrong_code", "uuid")
        );
    }
    
    // --- loginPreCheck tests ---
    
    @Test
    @DisplayName("登录前置校验 - 成功")
    void loginPreCheck_Success() {
        when(configService.selectConfigByKey(anyString())).thenReturn("192.168.1.1");
        mockedIpUtils.when(IpUtils::getIpAddr).thenReturn("127.0.0.1");

        assertDoesNotThrow(() -> sysLoginService.loginPreCheck("gooduser", "goodpass"));
    }
    
    @Test
    @DisplayName("登录前置校验 - 用户名或密码为空")
    void loginPreCheck_NullCredentials() {
        assertThrows(UserNotExistsException.class, () -> sysLoginService.loginPreCheck(null, "pass"));
        assertThrows(UserNotExistsException.class, () -> sysLoginService.loginPreCheck("user", null));
    }
    
    @Test
    @DisplayName("登录前置校验 - 密码长度不匹配")
    void loginPreCheck_PasswordLength() {
        assertThrows(UserPasswordNotMatchException.class, () -> sysLoginService.loginPreCheck("user", "1234"));
    }
    
    @Test
    @DisplayName("登录前置校验 - 用户名长度不匹配")
    void loginPreCheck_UsernameLength() {
        assertThrows(UserPasswordNotMatchException.class, () -> sysLoginService.loginPreCheck("u", "password"));
    }
    
    @Test
    @DisplayName("登录前置校验 - IP被列入黑名单")
    void loginPreCheck_IpBlacklisted() {
        String blockedIp = "192.168.1.100";
        when(configService.selectConfigByKey("sys.login.blackIPList")).thenReturn(blockedIp);
        mockedIpUtils.when(IpUtils::getIpAddr).thenReturn(blockedIp);
        mockedIpUtils.when(() -> IpUtils.isMatchedIp(blockedIp, blockedIp)).thenReturn(true);

        assertThrows(BlackListException.class, () -> sysLoginService.loginPreCheck("user", "password"));
    }
    
    // --- recordLoginInfo test ---
    
    @Test
    @DisplayName("记录登录信息")
    void recordLoginInfo() {
        Long userId = 1L;
        sysLoginService.recordLoginInfo(userId);
        verify(userService).updateUserProfile(any(SysUser.class));
    }
} 