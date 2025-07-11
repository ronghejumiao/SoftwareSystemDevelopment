package com.ruoyi.system.service.impl;

import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.utils.DictUtils;
import com.ruoyi.system.mapper.SysDictDataMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SysDictDataServiceImplTest {

    @Mock
    private SysDictDataMapper dictDataMapper;

    @InjectMocks
    private SysDictDataServiceImpl service;

    private SysDictData data;

    @BeforeEach
    void setUp() {
        data = new SysDictData();
        data.setDictCode(1L);
        data.setDictType("typeA");
        data.setDictValue("v1");
    }

    @Test
    void selectDictDataList_shouldDelegate() {
        List<SysDictData> list = Collections.singletonList(data);
        when(dictDataMapper.selectDictDataList(any())).thenReturn(list);
        assertEquals(list, service.selectDictDataList(new SysDictData()));
    }

    @Test
    void selectDictLabel_shouldDelegate() {
        when(dictDataMapper.selectDictLabel("typeA", "v1")).thenReturn("标签A");
        assertEquals("标签A", service.selectDictLabel("typeA", "v1"));
    }

    @Test
    void selectDictDataById_shouldDelegate() {
        when(dictDataMapper.selectDictDataById(1L)).thenReturn(data);
        assertEquals(data, service.selectDictDataById(1L));
    }

    @Test
    void deleteDictDataByIds_shouldUpdateCache() {
        SysDictData d2 = new SysDictData();
        d2.setDictCode(2L);
        d2.setDictType("typeA");
        List<SysDictData> dictList = Arrays.asList(data, d2);

        when(dictDataMapper.selectDictDataById(1L)).thenReturn(data);
        when(dictDataMapper.selectDictDataById(2L)).thenReturn(d2);
        when(dictDataMapper.selectDictDataByType("typeA")).thenReturn(dictList);

        try (MockedStatic<DictUtils> utils = mockStatic(DictUtils.class)) {
            service.deleteDictDataByIds(new Long[]{1L, 2L});
            verify(dictDataMapper).deleteDictDataById(1L);
            verify(dictDataMapper).deleteDictDataById(2L);
            verify(dictDataMapper, times(2)).selectDictDataByType("typeA");
            utils.verify(() -> DictUtils.setDictCache(eq("typeA"), eq(dictList)), times(2));
        }
    }

    @Test
    void insertDictData_shouldUpdateCacheOnSuccess() {
        List<SysDictData> dictList = Collections.singletonList(data);
        when(dictDataMapper.insertDictData(data)).thenReturn(1);
        when(dictDataMapper.selectDictDataByType("typeA")).thenReturn(dictList);

        try (MockedStatic<DictUtils> utils = mockStatic(DictUtils.class)) {
            assertEquals(1, service.insertDictData(data));
            utils.verify(() -> DictUtils.setDictCache("typeA", dictList));
        }
    }

    @Test
    void insertDictData_fail_shouldNotUpdateCache() {
        when(dictDataMapper.insertDictData(data)).thenReturn(0);
        assertEquals(0, service.insertDictData(data));
    }

    @Test
    void updateDictData_shouldUpdateCacheOnSuccess() {
        List<SysDictData> dictList = Collections.singletonList(data);
        when(dictDataMapper.updateDictData(data)).thenReturn(1);
        when(dictDataMapper.selectDictDataByType("typeA")).thenReturn(dictList);

        try (MockedStatic<DictUtils> utils = mockStatic(DictUtils.class)) {
            assertEquals(1, service.updateDictData(data));
            utils.verify(() -> DictUtils.setDictCache("typeA", dictList));
        }
    }

    @Test
    void updateDictData_fail_shouldNotUpdateCache() {
        when(dictDataMapper.updateDictData(data)).thenReturn(0);
        assertEquals(0, service.updateDictData(data));
    }


}