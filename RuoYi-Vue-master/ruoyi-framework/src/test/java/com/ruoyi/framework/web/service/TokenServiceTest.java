package com.ruoyi.framework.web.service;

import com.ruoyi.common.constant.CacheConstants;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.common.utils.ip.AddressUtils;
import com.ruoyi.common.utils.ip.IpUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * 针对 TokenService 的单元测试类
 *
 * @author Gemini
 */
@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    @Mock
    private RedisCache redisCache;
    @Mock
    private HttpServletRequest request;

    // --- 静态类模拟 ---
    private MockedStatic<IdUtils> idUtilsMockedStatic;
    private MockedStatic<ServletUtils> servletUtilsMockedStatic;
    private MockedStatic<IpUtils> ipUtilsMockedStatic;
    private MockedStatic<AddressUtils> addressUtilsMockedStatic;
    private MockedStatic<UserAgent> userAgentMockedStatic;
    private MockedStatic<Jwts> jwtsMockedStatic;

    // --- 常量 ---
    private final String MOCK_UUID = "mock-uuid-12345";
    private final String MOCK_JWT = "mock.jwt.token";
    private final String TOKEN_SECRET = "a-very-long-and-secure-secret-key-for-testing-hs512";
    private final int EXPIRE_TIME_MINUTES = 30;
    private final String TOKEN_HEADER = "Authorization";

    @BeforeEach
    void setUp() throws Exception {
        // --- 初始化所有静态模拟 ---
        idUtilsMockedStatic = mockStatic(IdUtils.class);
        servletUtilsMockedStatic = mockStatic(ServletUtils.class);
        ipUtilsMockedStatic = mockStatic(IpUtils.class);
        addressUtilsMockedStatic = mockStatic(AddressUtils.class);
        userAgentMockedStatic = mockStatic(UserAgent.class);
        jwtsMockedStatic = mockStatic(Jwts.class);

        // --- 手动设置 @Value 注入的字段 ---
        setField("header", TOKEN_HEADER);
        setField("secret", TOKEN_SECRET);
        setField("expireTime", EXPIRE_TIME_MINUTES);

        // --- 为静态方法设置通用行为 ---
        idUtilsMockedStatic.when(IdUtils::fastUUID).thenReturn(MOCK_UUID);
        ipUtilsMockedStatic.when(IpUtils::getIpAddr).thenReturn("127.0.0.1");
        addressUtilsMockedStatic.when(() -> AddressUtils.getRealAddressByIP(anyString())).thenReturn("内网IP");
    }

    private void setField(String fieldName, Object value) throws Exception {
        Field field = TokenService.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(tokenService, value);
    }

    @AfterEach
    void tearDown() {
        // 关闭所有静态模拟
        idUtilsMockedStatic.close();
        servletUtilsMockedStatic.close();
        ipUtilsMockedStatic.close();
        addressUtilsMockedStatic.close();
        userAgentMockedStatic.close();
        jwtsMockedStatic.close();
    }

    @Test
    @DisplayName("createToken 应能成功创建并缓存Token")
    void createToken_ShouldCreateAndCacheTokenSuccessfully() {
        // Arrange
        SysUser sysUser = new SysUser();
        sysUser.setUserName("testuser");
        LoginUser loginUser = new LoginUser(1L, 1L, sysUser, new HashSet<>());

        UserAgent mockUserAgent = mock(UserAgent.class);
        Browser mockBrowser = mock(Browser.class);
        OperatingSystem mockOs = mock(OperatingSystem.class);
        userAgentMockedStatic.when(() -> UserAgent.parseUserAgentString(anyString())).thenReturn(mockUserAgent);
        when(mockUserAgent.getBrowser()).thenReturn(mockBrowser);
        when(mockBrowser.getName()).thenReturn("Chrome");
        when(mockUserAgent.getOperatingSystem()).thenReturn(mockOs);
        when(mockOs.getName()).thenReturn("Windows 10");

        servletUtilsMockedStatic.when(ServletUtils::getRequest).thenReturn(request);
        when(request.getHeader("User-Agent")).thenReturn("Mock-Agent/1.0");

        JwtBuilder mockBuilder = mock(JwtBuilder.class);
        jwtsMockedStatic.when(Jwts::builder).thenReturn(mockBuilder);
        when(mockBuilder.setClaims(anyMap())).thenReturn(mockBuilder);
        when(mockBuilder.signWith(any(), anyString())).thenReturn(mockBuilder);
        when(mockBuilder.compact()).thenReturn(MOCK_JWT);

        // Act
        String token = tokenService.createToken(loginUser);

        // Assert
        assertEquals(MOCK_JWT, token);
        verify(redisCache).setCacheObject(
                eq(CacheConstants.LOGIN_TOKEN_KEY + MOCK_UUID),
                any(LoginUser.class),
                eq(EXPIRE_TIME_MINUTES),
                eq(TimeUnit.MINUTES)
        );
    }

    // --- 【方法覆盖率补充】新增以下测试用例 ---

    @Test
    @DisplayName("setLoginUser 应刷新缓存中的用户信息")
    void setLoginUser_ShouldRefreshTokenInCache() {
        // Arrange
        LoginUser loginUser = new LoginUser();
        loginUser.setToken(MOCK_UUID);

        // Act
        tokenService.setLoginUser(loginUser);

        // Assert
        // 验证 refreshToken 方法被间接调用，即 redisCache.setCacheObject 被调用
        verify(redisCache, times(1)).setCacheObject(
                eq(CacheConstants.LOGIN_TOKEN_KEY + MOCK_UUID),
                eq(loginUser),
                eq(EXPIRE_TIME_MINUTES),
                eq(TimeUnit.MINUTES)
        );
    }

    @Test
    @DisplayName("delLoginUser 应删除Redis中的用户缓存")
    void delLoginUser_ShouldDeleteTokenFromCache() {
        // Act
        tokenService.delLoginUser(MOCK_UUID);

        // Assert
        verify(redisCache, times(1)).deleteObject(CacheConstants.LOGIN_TOKEN_KEY + MOCK_UUID);
    }

    @Test
    @DisplayName("getUsernameFromToken 应能从Token中解析出用户名")
    void getUsernameFromToken_ShouldReturnUsername() {
        // Arrange
        String expectedUsername = "testuser";
        // 模拟JWT解析器
        JwtParser mockParser = mock(JwtParser.class);
        Jws<Claims> mockJws = mock(Jws.class);
        Claims mockClaims = mock(Claims.class);
        jwtsMockedStatic.when(Jwts::parser).thenReturn(mockParser);
        when(mockParser.setSigningKey(anyString())).thenReturn(mockParser);
        when(mockParser.parseClaimsJws(MOCK_JWT)).thenReturn(mockJws);
        when(mockJws.getBody()).thenReturn(mockClaims);
        // 核心模拟：当获取 subject 时返回我们的用户名
        when(mockClaims.getSubject()).thenReturn(expectedUsername);

        // Act
        String actualUsername = tokenService.getUsernameFromToken(MOCK_JWT);

        // Assert
        assertEquals(expectedUsername, actualUsername);
    }

    // --- 已有测试用例保持不变 ---

    @Test
    @DisplayName("verifyToken 当Token即将过期时应刷新")
    void verifyToken_WhenTokenAboutToExpire_ShouldRefreshToken() {
        // Arrange
        LoginUser loginUser = new LoginUser();
        loginUser.setToken(MOCK_UUID);
        long expireTimestamp = System.currentTimeMillis() + 19 * 60 * 1000L;
        loginUser.setExpireTime(expireTimestamp);

        // Act
        tokenService.verifyToken(loginUser);

        // Assert
        verify(redisCache, times(1)).setCacheObject(anyString(), any(LoginUser.class), anyInt(), any(TimeUnit.class));
    }

    @Test
    @DisplayName("getLoginUser 当Token有效时应返回用户信息")
    @SuppressWarnings("unchecked")
    void getLoginUser_WithValidToken_ShouldReturnLoginUser() {
        // Arrange
        String fullToken = Constants.TOKEN_PREFIX + MOCK_JWT;
        when(request.getHeader(TOKEN_HEADER)).thenReturn(fullToken);

        JwtParser mockParser = mock(JwtParser.class);
        Jws<Claims> mockJws = mock(Jws.class);
        Claims mockClaims = mock(Claims.class);
        jwtsMockedStatic.when(Jwts::parser).thenReturn(mockParser);
        when(mockParser.setSigningKey(anyString())).thenReturn(mockParser);
        when(mockParser.parseClaimsJws(MOCK_JWT)).thenReturn(mockJws);
        when(mockJws.getBody()).thenReturn(mockClaims);

        when(mockClaims.get(Constants.LOGIN_USER_KEY)).thenReturn(MOCK_UUID);
        LoginUser expectedUser = new LoginUser(2L, 2L, new SysUser(), new HashSet<>());
        when(redisCache.getCacheObject(CacheConstants.LOGIN_TOKEN_KEY + MOCK_UUID)).thenReturn(expectedUser);

        // Act
        LoginUser actualUser = tokenService.getLoginUser(request);

        // Assert
        assertSame(expectedUser, actualUser);
    }
}