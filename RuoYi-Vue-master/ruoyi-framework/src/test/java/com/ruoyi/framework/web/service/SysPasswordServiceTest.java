// 最终修复版的 SysPasswordServiceTest.java
package com.ruoyi.framework.web.service;

import com.ruoyi.common.constant.CacheConstants;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.user.UserPasswordNotMatchException;
import com.ruoyi.common.exception.user.UserPasswordRetryLimitExceedException;
import com.ruoyi.common.utils.MessageUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.framework.security.context.AuthenticationContextHolder;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * 针对 SysPasswordService 的单元测试类
 *
 * @author Gemini
 */
@ExtendWith(MockitoExtension.class)
class SysPasswordServiceTest {

    @InjectMocks
    private SysPasswordService sysPasswordService;

    @Mock
    private RedisCache redisCache;

    // 【修复2】添加缺失的静态类模拟
    private MockedStatic<SpringUtils> springUtilsMockedStatic;
    private MockedStatic<MessageUtils> messageUtilsMockedStatic;
    private MockedStatic<AuthenticationContextHolder> authenticationContextHolderMockedStatic;
    private MockedStatic<SecurityUtils> securityUtilsMockedStatic;

    private final String TEST_USERNAME = "testuser";
    private final String TEST_PASSWORD = "password123";
    private final int MAX_RETRY_COUNT = 5;
    private final int LOCK_TIME = 10;

    @BeforeEach
    void setUp() throws Exception {
        // 【修复2】模拟静态类，必须先模拟最底层的依赖
        springUtilsMockedStatic = mockStatic(SpringUtils.class);
        messageUtilsMockedStatic = mockStatic(MessageUtils.class);
        authenticationContextHolderMockedStatic = mockStatic(AuthenticationContextHolder.class);
        securityUtilsMockedStatic = mockStatic(SecurityUtils.class);

        // 准备一个模拟的Authentication对象
        Authentication mockAuthentication = mock(Authentication.class);
        // 【修复1】使用 lenient() 来避免 UnnecessaryStubbingException
        lenient().when(mockAuthentication.getName()).thenReturn(TEST_USERNAME);
        lenient().when(mockAuthentication.getCredentials()).thenReturn(TEST_PASSWORD);
        authenticationContextHolderMockedStatic.when(AuthenticationContextHolder::getContext).thenReturn(mockAuthentication);
        // 【修复2】为MessageUtils提供默认行为
        when(MessageUtils.message(anyString(), any())).thenAnswer(invocation -> invocation.getArgument(0));


        // 通过反射手动注入@Value字段的值
        setField(sysPasswordService, "maxRetryCount", MAX_RETRY_COUNT);
        setField(sysPasswordService, "lockTime", LOCK_TIME);
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @AfterEach
    void tearDown() {
        // 关闭所有静态模拟
        springUtilsMockedStatic.close();
        messageUtilsMockedStatic.close();
        authenticationContextHolderMockedStatic.close();
        securityUtilsMockedStatic.close();
    }

    @Nested
    @DisplayName("validate 方法测试")
    class ValidateTest {

        @Test
        @DisplayName("密码正确且无重试记录时，应直接通过")
        void withCorrectPasswordAndNoRetryHistory_ShouldPass() {
            SysUser user = new SysUser();
            user.setPassword("encryptedPassword");

            when(redisCache.getCacheObject(anyString())).thenReturn(null);
            securityUtilsMockedStatic.when(() -> SecurityUtils.matchesPassword(TEST_PASSWORD, user.getPassword())).thenReturn(true);
            // 【修复3】因为无记录，所以hasKey应返回false
            when(redisCache.hasKey(anyString())).thenReturn(false);

            assertDoesNotThrow(() -> sysPasswordService.validate(user));

            // 【修复3】验证deleteObject【从未】被调用，因为hasKey返回false
            verify(redisCache, never()).deleteObject(anyString());
        }

        @Test
        @DisplayName("密码正确且有重试记录时，应清除重试记录")
        void withCorrectPasswordAndExistingRetries_ShouldClearCache() {
            SysUser user = new SysUser();
            user.setPassword("encryptedPassword");

            when(redisCache.getCacheObject(anyString())).thenReturn(2);
            securityUtilsMockedStatic.when(() -> SecurityUtils.matchesPassword(anyString(), anyString())).thenReturn(true);
            // 【修复3】要清除记录，前提是hasKey返回true
            when(redisCache.hasKey(anyString())).thenReturn(true);

            sysPasswordService.validate(user);

            verify(redisCache).deleteObject(CacheConstants.PWD_ERR_CNT_KEY + TEST_USERNAME);
            verify(redisCache, never()).setCacheObject(anyString(), any(), (int) anyLong(), any(TimeUnit.class));
        }

        @Test
        @DisplayName("密码错误且无重试记录时，应增加重试次数为1")
        void withIncorrectPasswordAndNoRetryHistory_ShouldIncrementRetryCount() {
            SysUser user = new SysUser();
            user.setPassword("encryptedPassword");

            when(redisCache.getCacheObject(anyString())).thenReturn(null);
            securityUtilsMockedStatic.when(() -> SecurityUtils.matchesPassword(anyString(), anyString())).thenReturn(false);

            assertThrows(UserPasswordNotMatchException.class, () -> sysPasswordService.validate(user));

            ArgumentCaptor<Integer> retryCountCaptor = ArgumentCaptor.forClass(Integer.class);
            verify(redisCache).setCacheObject(eq(CacheConstants.PWD_ERR_CNT_KEY + TEST_USERNAME), retryCountCaptor.capture(), eq(LOCK_TIME), eq(TimeUnit.MINUTES));
            assertEquals(1, retryCountCaptor.getValue());
        }

        @Test
        @DisplayName("密码错误且已有重试记录时，应累加重试次数")
        void withIncorrectPasswordAndExistingRetries_ShouldIncrementRetryCount() {
            SysUser user = new SysUser();
            user.setPassword("encryptedPassword");
            int initialRetryCount = 3;

            when(redisCache.getCacheObject(anyString())).thenReturn(initialRetryCount);
            securityUtilsMockedStatic.when(() -> SecurityUtils.matchesPassword(anyString(), anyString())).thenReturn(false);

            assertThrows(UserPasswordNotMatchException.class, () -> sysPasswordService.validate(user));

            ArgumentCaptor<Integer> retryCountCaptor = ArgumentCaptor.forClass(Integer.class);
            verify(redisCache).setCacheObject(anyString(), retryCountCaptor.capture(), eq(LOCK_TIME), any(TimeUnit.class));
            assertEquals(initialRetryCount + 1, retryCountCaptor.getValue());
        }

        @Test
        @DisplayName("当重试次数达到上限时，应抛出锁定异常")
        void whenRetryCountReachesMax_ShouldThrowLimitExceedException() {
            SysUser user = new SysUser();
            when(redisCache.getCacheObject(anyString())).thenReturn(MAX_RETRY_COUNT);

            UserPasswordRetryLimitExceedException exception = assertThrows(UserPasswordRetryLimitExceedException.class, () -> {
                sysPasswordService.validate(user);
            });

            // 因为mock了MessageUtils，getMessage()会返回消息代码
            assertEquals("user.password.retry.limit.exceed", exception.getMessage());

            verify(redisCache, never()).setCacheObject(anyString(), any(), anyInt(), any(TimeUnit.class));
        }
    }

    @Nested
    @DisplayName("clearLoginRecordCache 方法测试")
    class ClearLoginRecordCacheTest {
        @Test
        @DisplayName("当缓存键存在时，应删除缓存")
        void whenKeyExists_ShouldDeleteCache() {
            when(redisCache.hasKey(anyString())).thenReturn(true);

            sysPasswordService.clearLoginRecordCache(TEST_USERNAME);

            verify(redisCache).deleteObject(CacheConstants.PWD_ERR_CNT_KEY + TEST_USERNAME);
        }

        @Test
        @DisplayName("当缓存键不存在时，不应执行删除操作")
        void whenKeyDoesNotExist_ShouldNotDeleteCache() {
            when(redisCache.hasKey(anyString())).thenReturn(false);

            sysPasswordService.clearLoginRecordCache(TEST_USERNAME);

            verify(redisCache, never()).deleteObject(anyString());
        }
    }
}