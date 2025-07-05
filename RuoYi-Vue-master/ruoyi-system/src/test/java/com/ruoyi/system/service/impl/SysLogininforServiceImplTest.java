package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.SysLogininfor;
import com.ruoyi.system.mapper.SysLogininforMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SysLogininforServiceImplTest {

    @Mock
    private SysLogininforMapper logininforMapper;

    @InjectMocks
    private SysLogininforServiceImpl service;

    private SysLogininfor info;

    @BeforeEach
    void setUp() {
        info = new SysLogininfor();
        info.setInfoId(1L);
        info.setIpaddr("127.0.0.1");
    }

    @Test
    void insertLogininfor_shouldDelegate() {
        service.insertLogininfor(info);
        verify(logininforMapper).insertLogininfor(info);
    }

    @Test
    void selectLogininforList_shouldDelegate() {
        List<SysLogininfor> list = Collections.singletonList(info);
        when(logininforMapper.selectLogininforList(any())).thenReturn(list);
        assertEquals(list, service.selectLogininforList(new SysLogininfor()));
    }

    @Test
    void deleteLogininforByIds_shouldDelegate() {
        when(logininforMapper.deleteLogininforByIds(any())).thenReturn(2);
        assertEquals(2, service.deleteLogininforByIds(new Long[]{1L, 2L}));
    }

    @Test
    void cleanLogininfor_shouldDelegate() {
        service.cleanLogininfor();
        verify(logininforMapper).cleanLogininfor();
    }

    @Test
    void allMapperMethods_throwException_shouldPropagate() {
        RuntimeException ex = new RuntimeException("db error");
        doThrow(ex).when(logininforMapper).insertLogininfor(any());
        when(logininforMapper.selectLogininforList(any())).thenThrow(ex);
        when(logininforMapper.deleteLogininforByIds(any())).thenThrow(ex);
        doThrow(ex).when(logininforMapper).cleanLogininfor();

        assertThrows(RuntimeException.class, () -> service.insertLogininfor(info));
        assertThrows(RuntimeException.class, () -> service.selectLogininforList(new SysLogininfor()));
        assertThrows(RuntimeException.class, () -> service.deleteLogininforByIds(new Long[]{1L}));
        assertThrows(RuntimeException.class, () -> service.cleanLogininfor());
    }
}