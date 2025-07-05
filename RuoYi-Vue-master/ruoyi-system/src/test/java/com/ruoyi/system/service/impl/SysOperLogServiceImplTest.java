package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.SysOperLog;
import com.ruoyi.system.mapper.SysOperLogMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SysOperLogServiceImplTest {

    @Mock
    private SysOperLogMapper operLogMapper;

    @InjectMocks
    private SysOperLogServiceImpl service;

    private SysOperLog log;

    @BeforeEach
    void setUp() {
        log = new SysOperLog();
        log.setOperId(1L);
        log.setTitle("操作1");
    }

    @Test
    void insertOperlog_shouldDelegate() {
        service.insertOperlog(log);
        verify(operLogMapper).insertOperlog(log);
    }

    @Test
    void selectOperLogList_shouldDelegate() {
        List<SysOperLog> list = Collections.singletonList(log);
        when(operLogMapper.selectOperLogList(any())).thenReturn(list);
        assertEquals(list, service.selectOperLogList(new SysOperLog()));
    }

    @Test
    void deleteOperLogByIds_shouldDelegate() {
        when(operLogMapper.deleteOperLogByIds(any())).thenReturn(2);
        assertEquals(2, service.deleteOperLogByIds(new Long[]{1L, 2L}));
    }

    @Test
    void selectOperLogById_shouldDelegate() {
        when(operLogMapper.selectOperLogById(1L)).thenReturn(log);
        assertEquals(log, service.selectOperLogById(1L));
    }

    @Test
    void cleanOperLog_shouldDelegate() {
        service.cleanOperLog();
        verify(operLogMapper).cleanOperLog();
    }

    @Test
    void allMapperMethods_throwException_shouldPropagate() {
        RuntimeException ex = new RuntimeException("db error");
        doThrow(ex).when(operLogMapper).insertOperlog(any());
        when(operLogMapper.selectOperLogList(any())).thenThrow(ex);
        when(operLogMapper.deleteOperLogByIds(any())).thenThrow(ex);
        when(operLogMapper.selectOperLogById(anyLong())).thenThrow(ex);
        doThrow(ex).when(operLogMapper).cleanOperLog();

        assertThrows(RuntimeException.class, () -> service.insertOperlog(log));
        assertThrows(RuntimeException.class, () -> service.selectOperLogList(new SysOperLog()));
        assertThrows(RuntimeException.class, () -> service.deleteOperLogByIds(new Long[]{1L}));
        assertThrows(RuntimeException.class, () -> service.selectOperLogById(1L));
        assertThrows(RuntimeException.class, () -> service.cleanOperLog());
    }
}