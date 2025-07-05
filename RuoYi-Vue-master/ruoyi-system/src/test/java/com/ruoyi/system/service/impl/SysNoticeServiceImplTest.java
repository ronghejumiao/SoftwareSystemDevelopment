package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.SysNotice;
import com.ruoyi.system.mapper.SysNoticeMapper;
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
class SysNoticeServiceImplTest {

    @Mock
    private SysNoticeMapper noticeMapper;

    @InjectMocks
    private SysNoticeServiceImpl service;

    private SysNotice notice;

    @BeforeEach
    void setUp() {
        notice = new SysNotice();
        notice.setNoticeId(1L);
        notice.setNoticeTitle("公告1");
    }

    @Test
    void selectNoticeById_shouldDelegate() {
        when(noticeMapper.selectNoticeById(1L)).thenReturn(notice);
        assertEquals(notice, service.selectNoticeById(1L));
    }

    @Test
    void selectNoticeList_shouldDelegate() {
        List<SysNotice> list = Collections.singletonList(notice);
        when(noticeMapper.selectNoticeList(any())).thenReturn(list);
        assertEquals(list, service.selectNoticeList(new SysNotice()));
    }

    @Test
    void insertNotice_shouldDelegate() {
        when(noticeMapper.insertNotice(notice)).thenReturn(1);
        assertEquals(1, service.insertNotice(notice));
    }

    @Test
    void updateNotice_shouldDelegate() {
        when(noticeMapper.updateNotice(notice)).thenReturn(1);
        assertEquals(1, service.updateNotice(notice));
    }

    @Test
    void deleteNoticeById_shouldDelegate() {
        when(noticeMapper.deleteNoticeById(1L)).thenReturn(1);
        assertEquals(1, service.deleteNoticeById(1L));
    }

    @Test
    void deleteNoticeByIds_shouldDelegate() {
        when(noticeMapper.deleteNoticeByIds(any())).thenReturn(2);
        assertEquals(2, service.deleteNoticeByIds(new Long[]{1L, 2L}));
    }

    @Test
    void allMapperMethods_throwException_shouldPropagate() {
        RuntimeException ex = new RuntimeException("db error");
        when(noticeMapper.selectNoticeById(anyLong())).thenThrow(ex);
        when(noticeMapper.selectNoticeList(any())).thenThrow(ex);
        when(noticeMapper.insertNotice(any())).thenThrow(ex);
        when(noticeMapper.updateNotice(any())).thenThrow(ex);
        when(noticeMapper.deleteNoticeById(anyLong())).thenThrow(ex);
        when(noticeMapper.deleteNoticeByIds(any())).thenThrow(ex);

        assertThrows(RuntimeException.class, () -> service.selectNoticeById(1L));
        assertThrows(RuntimeException.class, () -> service.selectNoticeList(new SysNotice()));
        assertThrows(RuntimeException.class, () -> service.insertNotice(notice));
        assertThrows(RuntimeException.class, () -> service.updateNotice(notice));
        assertThrows(RuntimeException.class, () -> service.deleteNoticeById(1L));
        assertThrows(RuntimeException.class, () -> service.deleteNoticeByIds(new Long[]{1L}));
    }
}