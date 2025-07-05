package com.ruoyi.system.service.impl;

import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.core.domain.entity.SysDictType;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DictUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.mapper.SysDictDataMapper;
import com.ruoyi.system.mapper.SysDictTypeMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SysDictTypeServiceImplTest {

    @Mock
    private SysDictTypeMapper dictTypeMapper;
    @Mock
    private SysDictDataMapper dictDataMapper;
    @InjectMocks
    private SysDictTypeServiceImpl service;

    private SysDictType dictType;

    @BeforeEach
    void setUp() {
        dictType = new SysDictType();
        dictType.setDictId(1L);
        dictType.setDictType("typeA");
        dictType.setDictName("类型A");
    }

    @Test
    void selectDictTypeList_shouldDelegate() {
        List<SysDictType> list = Collections.singletonList(dictType);
        when(dictTypeMapper.selectDictTypeList(any())).thenReturn(list);
        assertEquals(list, service.selectDictTypeList(new SysDictType()));
    }

    @Test
    void selectDictTypeAll_shouldDelegate() {
        List<SysDictType> list = Collections.singletonList(dictType);
        when(dictTypeMapper.selectDictTypeAll()).thenReturn(list);
        assertEquals(list, service.selectDictTypeAll());
    }

    @Test
    void selectDictDataByType_cacheHit() {
        List<SysDictData> datas = Collections.singletonList(new SysDictData());
        try (MockedStatic<DictUtils> utils = mockStatic(DictUtils.class)) {
            utils.when(() -> DictUtils.getDictCache("typeA")).thenReturn(datas);
            assertEquals(datas, service.selectDictDataByType("typeA"));
        }
    }

    @Test
    void selectDictDataByType_dbHit() {
        List<SysDictData> datas = Collections.singletonList(new SysDictData());
        try (MockedStatic<DictUtils> utils = mockStatic(DictUtils.class)) {
            utils.when(() -> DictUtils.getDictCache("typeA")).thenReturn(null);
            when(dictDataMapper.selectDictDataByType("typeA")).thenReturn(datas);
            utils.when(() -> DictUtils.setDictCache("typeA", datas)).then(inv -> null);
            assertEquals(datas, service.selectDictDataByType("typeA"));
        }
    }

    @Test
    void selectDictDataByType_notFound() {
        try (MockedStatic<DictUtils> utils = mockStatic(DictUtils.class)) {
            utils.when(() -> DictUtils.getDictCache("typeA")).thenReturn(null);
            when(dictDataMapper.selectDictDataByType("typeA")).thenReturn(null);
            assertNull(service.selectDictDataByType("typeA"));
        }
    }

    @Test
    void selectDictTypeById_shouldDelegate() {
        when(dictTypeMapper.selectDictTypeById(1L)).thenReturn(dictType);
        assertEquals(dictType, service.selectDictTypeById(1L));
    }

    @Test
    void selectDictTypeByType_shouldDelegate() {
        when(dictTypeMapper.selectDictTypeByType("typeA")).thenReturn(dictType);
        assertEquals(dictType, service.selectDictTypeByType("typeA"));
    }

    @Test
    void deleteDictTypeByIds_shouldThrowIfHasData() {
        SysDictType t = new SysDictType();
        t.setDictId(1L);
        t.setDictType("typeA");
        t.setDictName("类型A");
        when(dictTypeMapper.selectDictTypeById(1L)).thenReturn(t);
        when(dictDataMapper.countDictDataByType("typeA")).thenReturn(1);
        ServiceException ex = assertThrows(ServiceException.class, () -> service.deleteDictTypeByIds(new Long[]{1L}));
        assertTrue(ex.getMessage().contains("已分配"));
    }

    @Test
    void deleteDictTypeByIds_shouldDeleteAndRemoveCache() {
        SysDictType t = new SysDictType();
        t.setDictId(1L);
        t.setDictType("typeA");
        t.setDictName("类型A");
        when(dictTypeMapper.selectDictTypeById(1L)).thenReturn(t);
        when(dictDataMapper.countDictDataByType("typeA")).thenReturn(0);
        when(dictTypeMapper.deleteDictTypeById(1L)).thenReturn(1);
        try (MockedStatic<DictUtils> utils = mockStatic(DictUtils.class)) {
            service.deleteDictTypeByIds(new Long[]{1L});
            utils.verify(() -> DictUtils.removeDictCache("typeA"));
        }
    }

    @Test
    void loadingDictCache_shouldSetAll() {
        SysDictData d1 = new SysDictData(); d1.setDictType("t1"); d1.setDictSort(1L);
        SysDictData d2 = new SysDictData(); d2.setDictType("t1"); d2.setDictSort(2L);
        List<SysDictData> all = Arrays.asList(d1, d2);
        when(dictDataMapper.selectDictDataList(any())).thenReturn(all);
        try (MockedStatic<DictUtils> utils = mockStatic(DictUtils.class)) {
            service.loadingDictCache();
            utils.verify(() -> DictUtils.setDictCache(eq("t1"), anyList()));
        }
    }

    @Test
    void clearDictCache_shouldDelegate() {
        try (MockedStatic<DictUtils> utils = mockStatic(DictUtils.class)) {
            service.clearDictCache();
            utils.verify(DictUtils::clearDictCache);
        }
    }

    @Test
    void resetDictCache_shouldClearAndLoad() {
        SysDictTypeServiceImpl spy = spy(service);
        doNothing().when(spy).clearDictCache();
        doNothing().when(spy).loadingDictCache();
        spy.resetDictCache();
        verify(spy).clearDictCache();
        verify(spy).loadingDictCache();
    }

    @Test
    void insertDictType_shouldSetCacheOnSuccess() {
        SysDictType t = new SysDictType();
        t.setDictType("typeA");
        when(dictTypeMapper.insertDictType(t)).thenReturn(1);
        try (MockedStatic<DictUtils> utils = mockStatic(DictUtils.class)) {
            assertEquals(1, service.insertDictType(t));
            utils.verify(() -> DictUtils.setDictCache("typeA", null));
        }
    }

    @Test
    void insertDictType_fail_shouldNotSetCache() {
        SysDictType t = new SysDictType();
        t.setDictType("typeA");
        when(dictTypeMapper.insertDictType(t)).thenReturn(0);
        try (MockedStatic<DictUtils> utils = mockStatic(DictUtils.class)) {
            assertEquals(0, service.insertDictType(t));
            utils.verify(() -> DictUtils.setDictCache(anyString(), any()), never());
        }
    }

    @Test
    void updateDictType_shouldUpdateCacheOnSuccess() {
        SysDictType old = new SysDictType();
        old.setDictId(1L);
        old.setDictType("typeA");
        SysDictType t = new SysDictType();
        t.setDictId(1L);
        t.setDictType("typeB");
        List<SysDictData> datas = Arrays.asList(new SysDictData());
        when(dictTypeMapper.selectDictTypeById(1L)).thenReturn(old);
        when(dictTypeMapper.updateDictType(t)).thenReturn(1);
        when(dictDataMapper.selectDictDataByType("typeB")).thenReturn(datas);
        try (MockedStatic<DictUtils> utils = mockStatic(DictUtils.class)) {
            assertEquals(1, service.updateDictType(t));
            utils.verify(() -> DictUtils.setDictCache("typeB", datas));
        }
    }

    @Test
    void updateDictType_fail_shouldNotUpdateCache() {
        SysDictType old = new SysDictType();
        old.setDictId(1L);
        old.setDictType("typeA");
        SysDictType t = new SysDictType();
        t.setDictId(1L);
        t.setDictType("typeB");
        when(dictTypeMapper.selectDictTypeById(1L)).thenReturn(old);
        when(dictTypeMapper.updateDictType(t)).thenReturn(0);
        try (MockedStatic<DictUtils> utils = mockStatic(DictUtils.class)) {
            assertEquals(0, service.updateDictType(t));
            utils.verify(() -> DictUtils.setDictCache(anyString(), any()), never());
        }
    }

    @Test
    void checkDictTypeUnique_notUnique() {
        SysDictType input = new SysDictType();
        input.setDictId(1L);
        input.setDictType("typeA");
        SysDictType db = new SysDictType();
        db.setDictId(2L);
        when(dictTypeMapper.checkDictTypeUnique("typeA")).thenReturn(db);
        assertFalse(service.checkDictTypeUnique(input));
    }

    @Test
    void checkDictTypeUnique_unique() {
        SysDictType input = new SysDictType();
        input.setDictId(1L);
        input.setDictType("typeA");
        when(dictTypeMapper.checkDictTypeUnique("typeA")).thenReturn(null);
        assertTrue(service.checkDictTypeUnique(input));
    }

    @Test
    void allMapperMethods_throwException_shouldPropagate() {
        RuntimeException ex = new RuntimeException("db error");
        when(dictTypeMapper.selectDictTypeList(any())).thenThrow(ex);
        when(dictTypeMapper.selectDictTypeAll()).thenThrow(ex);
        when(dictTypeMapper.selectDictTypeById(anyLong())).thenThrow(ex);
        when(dictTypeMapper.selectDictTypeByType(anyString())).thenThrow(ex);
        when(dictTypeMapper.deleteDictTypeById(anyLong())).thenThrow(ex);
        when(dictTypeMapper.insertDictType(any())).thenThrow(ex);
        when(dictTypeMapper.updateDictType(any())).thenThrow(ex);
        when(dictTypeMapper.checkDictTypeUnique(anyString())).thenThrow(ex);
        when(dictDataMapper.countDictDataByType(anyString())).thenThrow(ex);
        when(dictDataMapper.selectDictDataByType(anyString())).thenThrow(ex);
        when(dictDataMapper.selectDictDataList(any())).thenThrow(ex);
        when(dictDataMapper.updateDictDataType(anyString(), anyString())).thenThrow(ex);

        assertThrows(RuntimeException.class, () -> service.selectDictTypeList(new SysDictType()));
        assertThrows(RuntimeException.class, () -> service.selectDictTypeAll());
        assertThrows(RuntimeException.class, () -> service.selectDictTypeById(1L));
        assertThrows(RuntimeException.class, () -> service.selectDictTypeByType("typeA"));
        assertThrows(RuntimeException.class, () -> service.deleteDictTypeByIds(new Long[]{1L}));
        assertThrows(RuntimeException.class, () -> service.insertDictType(new SysDictType()));
        assertThrows(RuntimeException.class, () -> service.updateDictType(new SysDictType()));
        assertThrows(RuntimeException.class, () -> service.checkDictTypeUnique(new SysDictType()));
        assertThrows(RuntimeException.class, () -> service.selectDictDataByType("typeA"));
        assertThrows(RuntimeException.class, () -> service.loadingDictCache());
    }
}