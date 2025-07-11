package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.Classinfo;
import com.ruoyi.system.mapper.ClassinfoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * ClassinfoServiceImpl 单元测试
 */
@ExtendWith(MockitoExtension.class)
class ClassinfoServiceImplTest {

    @Mock
    private ClassinfoMapper mapper;

    @InjectMocks
    private ClassinfoServiceImpl service;

    private Classinfo sample;

    @BeforeEach
    void setUp() {
        sample = new Classinfo();
        sample.setClassId(1L);
        sample.setClassName("一班");
    }

    @Test
    void insertClassinfo_shouldSetCreateTimeAndDelegateToMapper() {
        when(mapper.insertClassinfo(any(Classinfo.class))).thenReturn(1);

        int result = service.insertClassinfo(sample);

        assertEquals(1, result);
        assertNotNull(sample.getCreateTime(), "createTime 应自动填充");

        ArgumentCaptor<Classinfo> captor = ArgumentCaptor.forClass(Classinfo.class);
        verify(mapper).insertClassinfo(captor.capture());
        assertEquals("一班", captor.getValue().getClassName());
    }

    @Test
    void updateClassinfo_shouldSetUpdateTimeAndDelegateToMapper() {
        when(mapper.updateClassinfo(any(Classinfo.class))).thenReturn(1);

        int result = service.updateClassinfo(sample);

        assertEquals(1, result);
        assertNotNull(sample.getUpdateTime(), "updateTime 应自动填充");
        verify(mapper).updateClassinfo(sample);
    }

    @Test
    void selectList_and_selectById_delegateToMapper() {
        when(mapper.selectClassinfoList(any())).thenReturn(Collections.singletonList(sample));
        when(mapper.selectClassinfoByClassId(1L)).thenReturn(sample);

        List<Classinfo> list = service.selectClassinfoList(new Classinfo());
        Classinfo info = service.selectClassinfoByClassId(1L);

        assertEquals(1, list.size());
        assertEquals(sample, info);
        verify(mapper).selectClassinfoList(any());
        verify(mapper).selectClassinfoByClassId(1L);
    }

    @Test
    void deleteMethods_delegateToMapper() {
        when(mapper.deleteClassinfoByClassIds(any())).thenReturn(2);
        when(mapper.deleteClassinfoByClassId(1L)).thenReturn(1);

        int count = service.deleteClassinfoByClassIds(new Long[]{1L, 2L});
        int single = service.deleteClassinfoByClassId(1L);

        assertEquals(2, count);
        assertEquals(1, single);
        verify(mapper).deleteClassinfoByClassIds(any());
        verify(mapper).deleteClassinfoByClassId(1L);
    }

    @Test
    void insertClassinfo_nullInput_shouldThrowNPE() {
        assertThrows(NullPointerException.class, () -> service.insertClassinfo(null));
    }

    @Test
    void updateClassinfo_nullInput_shouldThrowNPE() {
        assertThrows(NullPointerException.class, () -> service.updateClassinfo(null));
    }


    @Test
    void selectClassinfoByClassId_mapperReturnsNull_shouldReturnNull() {
        when(mapper.selectClassinfoByClassId(anyLong())).thenReturn(null);
        Classinfo info = service.selectClassinfoByClassId(999L);
        assertNull(info);
    }

    @Test
    void deleteClassinfoByClassIds_emptyArray_shouldReturnZero() {
        when(mapper.deleteClassinfoByClassIds(any())).thenReturn(0);
        int result = service.deleteClassinfoByClassIds(new Long[0]);
        assertEquals(0, result);
    }

    @Test
    void deleteClassinfoByClassId_notFound_shouldReturnZero() {
        when(mapper.deleteClassinfoByClassId(anyLong())).thenReturn(0);
        int result = service.deleteClassinfoByClassId(999L);
        assertEquals(0, result);
    }

    @Test
    void mapperExceptions_shouldPropagate() {
        RuntimeException dbErr = new RuntimeException("DB error");
        when(mapper.insertClassinfo(any())).thenThrow(dbErr);
        when(mapper.updateClassinfo(any())).thenThrow(dbErr);
        when(mapper.selectClassinfoList(any())).thenThrow(dbErr);
        when(mapper.selectClassinfoByClassId(anyLong())).thenThrow(dbErr);
        when(mapper.deleteClassinfoByClassIds(any())).thenThrow(dbErr);
        when(mapper.deleteClassinfoByClassId(anyLong())).thenThrow(dbErr);

        assertThrows(RuntimeException.class, () -> service.insertClassinfo(sample));
        assertThrows(RuntimeException.class, () -> service.updateClassinfo(sample));
        assertThrows(RuntimeException.class, () -> service.selectClassinfoList(new Classinfo()));
        assertThrows(RuntimeException.class, () -> service.selectClassinfoByClassId(1L));
        assertThrows(RuntimeException.class, () -> service.deleteClassinfoByClassIds(new Long[]{1L}));
        assertThrows(RuntimeException.class, () -> service.deleteClassinfoByClassId(1L));
    }
} 