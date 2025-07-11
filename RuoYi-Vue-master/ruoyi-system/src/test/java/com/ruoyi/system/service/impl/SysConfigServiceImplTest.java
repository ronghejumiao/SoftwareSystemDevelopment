package com.ruoyi.system.service.impl;

import com.ruoyi.common.constant.CacheConstants;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.mapper.SysConfigMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SysConfigServiceImplTest {

    @Mock
    private SysConfigMapper configMapper;

    @Mock
    private RedisCache redisCache;

    @InjectMocks
    private SysConfigServiceImpl sysConfigService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void selectConfigById_shouldDelegate() {
        SysConfig config = new SysConfig();
        config.setConfigId(1L);
        when(configMapper.selectConfig(any(SysConfig.class))).thenReturn(config);
        assertEquals(config, sysConfigService.selectConfigById(1L));
    }

    @Test
    void selectConfigByKey_cacheHit() {
        when(redisCache.getCacheObject(anyString())).thenReturn("v");
        assertEquals("v", sysConfigService.selectConfigByKey("k"));
        verify(configMapper, never()).selectConfig(any());
    }

    @Test
    void selectConfigByKey_dbHit() {
        when(redisCache.getCacheObject(anyString())).thenReturn(null);
        SysConfig config = new SysConfig();
        config.setConfigValue("v2");
        when(configMapper.selectConfig(any())).thenReturn(config);
        assertEquals("v2", sysConfigService.selectConfigByKey("k2"));
        verify(redisCache).setCacheObject(contains("k2"), eq("v2"));
    }

    @Test
    void selectConfigByKey_notFound() {
        when(redisCache.getCacheObject(anyString())).thenReturn(null);
        when(configMapper.selectConfig(any())).thenReturn(null);
        assertEquals("", sysConfigService.selectConfigByKey("notfound"));
    }

    @Test
    void selectCaptchaEnabled_defaultTrue() {
        when(redisCache.getCacheObject(anyString())).thenReturn(null);
        when(configMapper.selectConfig(any())).thenReturn(null);
        assertTrue(sysConfigService.selectCaptchaEnabled());
    }

    @Test
    void selectCaptchaEnabled_false() {
        when(redisCache.getCacheObject(anyString())).thenReturn("false");
        assertFalse(sysConfigService.selectCaptchaEnabled());
    }

    @Test
    void selectConfigList_shouldDelegate() {
        List<SysConfig> list = Collections.singletonList(new SysConfig());
        when(configMapper.selectConfigList(any())).thenReturn(list);
        assertEquals(list, sysConfigService.selectConfigList(new SysConfig()));
    }

    @Test
    void insertConfig_shouldSetCache() {
        SysConfig config = new SysConfig();
        config.setConfigKey("k");
        config.setConfigValue("v");
        when(configMapper.insertConfig(config)).thenReturn(1);
        assertEquals(1, sysConfigService.insertConfig(config));
        verify(redisCache).setCacheObject(contains("k"), eq("v"));
    }

    @Test
    void insertConfig_fail_shouldNotSetCache() {
        SysConfig config = new SysConfig();
        config.setConfigKey("k");
        config.setConfigValue("v");
        when(configMapper.insertConfig(config)).thenReturn(0);
        assertEquals(0, sysConfigService.insertConfig(config));
        verify(redisCache, never()).setCacheObject(anyString(), any());
    }

    @Test
    void updateConfig_keyChanged_shouldDeleteOldCache() {
        SysConfig old = new SysConfig();
        old.setConfigId(1L);
        old.setConfigKey("oldKey");
        SysConfig upd = new SysConfig();
        upd.setConfigId(1L);
        upd.setConfigKey("newKey");
        upd.setConfigValue("v");
        when(configMapper.selectConfigById(1L)).thenReturn(old);
        when(configMapper.updateConfig(upd)).thenReturn(1);
        assertEquals(1, sysConfigService.updateConfig(upd));
        verify(redisCache).deleteObject(contains("oldKey"));
        verify(redisCache).setCacheObject(contains("newKey"), eq("v"));
    }

    @Test
    void updateConfig_keyNotChanged_shouldNotDeleteOldCache() {
        SysConfig old = new SysConfig();
        old.setConfigId(1L);
        old.setConfigKey("sameKey");
        SysConfig upd = new SysConfig();
        upd.setConfigId(1L);
        upd.setConfigKey("sameKey");
        upd.setConfigValue("v");
        when(configMapper.selectConfigById(1L)).thenReturn(old);
        when(configMapper.updateConfig(upd)).thenReturn(1);
        assertEquals(1, sysConfigService.updateConfig(upd));
        verify(redisCache, never()).deleteObject(anyString());
        verify(redisCache).setCacheObject(contains("sameKey"), eq("v"));
    }

    @Test
    void updateConfig_fail_shouldNotSetCache() {
        SysConfig old = new SysConfig();
        old.setConfigId(1L);
        old.setConfigKey("sameKey");
        SysConfig upd = new SysConfig();
        upd.setConfigId(1L);
        upd.setConfigKey("sameKey");
        upd.setConfigValue("v");
        when(configMapper.selectConfigById(1L)).thenReturn(old);
        when(configMapper.updateConfig(upd)).thenReturn(0);
        assertEquals(0, sysConfigService.updateConfig(upd));
        verify(redisCache, never()).setCacheObject(anyString(), any());
    }

    @Test
    void deleteConfigByIds_builtin_shouldThrow() {
        SysConfig builtin = new SysConfig();
        builtin.setConfigId(1L);
        builtin.setConfigType("Y");
        builtin.setConfigKey("sys.key");
        when(configMapper.selectConfig(any(SysConfig.class))).thenReturn(builtin);
        assertThrows(ServiceException.class, () -> sysConfigService.deleteConfigByIds(new Long[]{1L}));
    }

    @Test
    void deleteConfigByIds_normal() {
        SysConfig normal = new SysConfig();
        normal.setConfigId(2L);
        normal.setConfigType("N");
        normal.setConfigKey("sys.key2");
        when(configMapper.selectConfig(any(SysConfig.class))).thenReturn(normal);
        when(configMapper.deleteConfigById(2L)).thenReturn(1);
        sysConfigService.deleteConfigByIds(new Long[]{2L});
        verify(redisCache).deleteObject(contains("sys.key2"));
    }

    @Test
    void loadingConfigCache_shouldSetAll() {
        SysConfig c1 = new SysConfig(); c1.setConfigKey("k1"); c1.setConfigValue("v1");
        SysConfig c2 = new SysConfig(); c2.setConfigKey("k2"); c2.setConfigValue("v2");
        when(configMapper.selectConfigList(any())).thenReturn(Arrays.asList(c1, c2));
        sysConfigService.loadingConfigCache();
        verify(redisCache).setCacheObject(contains("k1"), eq("v1"));
        verify(redisCache).setCacheObject(contains("k2"), eq("v2"));
    }

    @Test
    void clearConfigCache_shouldDeleteAll() {
        Collection<String> keys = Arrays.asList("sys:config:k1", "sys:config:k2");
        when(redisCache.keys(anyString())).thenReturn(keys);
        sysConfigService.clearConfigCache();
        verify(redisCache).deleteObject(keys);
    }


    @Test
    void checkConfigKeyUnique_notUnique() {
        SysConfig input = new SysConfig();
        input.setConfigId(1L);
        input.setConfigKey("k");
        SysConfig db = new SysConfig();
        db.setConfigId(2L);
        when(configMapper.checkConfigKeyUnique("k")).thenReturn(db);
        assertFalse(sysConfigService.checkConfigKeyUnique(input));
    }

    @Test
    void checkConfigKeyUnique_unique() {
        SysConfig input = new SysConfig();
        input.setConfigId(1L);
        input.setConfigKey("k");
        when(configMapper.checkConfigKeyUnique("k")).thenReturn(null);
        assertTrue(sysConfigService.checkConfigKeyUnique(input));
    }

    @Test
    void allMapperMethods_throwException_shouldPropagate() {
        RuntimeException ex = new RuntimeException("db error");
        when(configMapper.selectConfig(any(SysConfig.class))).thenThrow(ex);
        when(configMapper.selectConfigList(any(SysConfig.class))).thenThrow(ex);
        when(configMapper.insertConfig(any())).thenThrow(ex);
        when(configMapper.updateConfig(any())).thenThrow(ex);
        when(configMapper.selectConfigById(anyLong())).thenThrow(ex);
        when(configMapper.deleteConfigById(anyLong())).thenThrow(ex);
        when(configMapper.checkConfigKeyUnique(anyString())).thenThrow(ex);
        assertThrows(RuntimeException.class, () -> sysConfigService.selectConfigById(1L));
        assertThrows(RuntimeException.class, () -> sysConfigService.selectConfigList(new SysConfig()));
        assertThrows(RuntimeException.class, () -> sysConfigService.insertConfig(new SysConfig()));
        assertThrows(RuntimeException.class, () -> sysConfigService.updateConfig(new SysConfig()));
        assertThrows(RuntimeException.class, () -> sysConfigService.deleteConfigByIds(new Long[]{1L}));
    }
}