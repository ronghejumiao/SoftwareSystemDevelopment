package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.VideoResource;
import com.ruoyi.system.mapper.VideoResourceMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * VideoResourceServiceImpl 单元测试
 */
@ExtendWith(MockitoExtension.class)
class VideoResourceServiceImplTest {

    @Mock
    private VideoResourceMapper mapper;

    @InjectMocks
    private VideoResourceServiceImpl service;

    @Test
    void crudMethods_shouldDelegateToMapper() {
        VideoResource resource = new VideoResource();
        resource.setVideoId(1L);

        when(mapper.selectVideoResourceByVideoId(1L)).thenReturn(resource);
        when(mapper.insertVideoResource(resource)).thenReturn(1);
        when(mapper.updateVideoResource(resource)).thenReturn(1);
        when(mapper.deleteVideoResourceByVideoId(1L)).thenReturn(1);
        when(mapper.deleteVideoResourceByVideoIds(any())).thenReturn(2);
        when(mapper.selectVideoResourceList(any())).thenReturn(Collections.singletonList(resource));

        assertEquals(resource, service.selectVideoResourceByVideoId(1L));
        assertEquals(1, service.insertVideoResource(resource));
        assertEquals(1, service.updateVideoResource(resource));
        assertEquals(1, service.deleteVideoResourceByVideoId(1L));
        assertEquals(2, service.deleteVideoResourceByVideoIds(new Long[]{1L,2L}));
        assertEquals(1, service.selectVideoResourceList(new VideoResource()).size());

        verify(mapper).selectVideoResourceByVideoId(1L);
        verify(mapper).insertVideoResource(resource);
        verify(mapper).updateVideoResource(resource);
        verify(mapper).deleteVideoResourceByVideoId(1L);
        verify(mapper).deleteVideoResourceByVideoIds(any());
        verify(mapper).selectVideoResourceList(any());
    }

    @Test
    void selectVideoResourceByVideoId_mapperReturnsNull_shouldReturnNull() {
        when(mapper.selectVideoResourceByVideoId(99L)).thenReturn(null);
        assertNull(service.selectVideoResourceByVideoId(99L));
    }

    @Test
    void selectVideoResourceList_mapperReturnsNull_shouldReturnNull() {
        when(mapper.selectVideoResourceList(any())).thenReturn(null);
        assertNull(service.selectVideoResourceList(new VideoResource()));
    }

    @Test
    void deleteVideoResourceByVideoIds_emptyArray_shouldReturnZero() {
        when(mapper.deleteVideoResourceByVideoIds(new Long[0])).thenReturn(0);
        assertEquals(0, service.deleteVideoResourceByVideoIds(new Long[0]));
    }

    @Test
    void allMapperMethods_throwException_shouldPropagate() {
        RuntimeException ex = new RuntimeException("db error");
        when(mapper.selectVideoResourceByVideoId(anyLong())).thenThrow(ex);
        when(mapper.selectVideoResourceList(any())).thenThrow(ex);
        when(mapper.insertVideoResource(any())).thenThrow(ex);
        when(mapper.updateVideoResource(any())).thenThrow(ex);
        when(mapper.deleteVideoResourceByVideoIds(any())).thenThrow(ex);
        when(mapper.deleteVideoResourceByVideoId(anyLong())).thenThrow(ex);
        assertThrows(RuntimeException.class, () -> service.selectVideoResourceByVideoId(1L));
        assertThrows(RuntimeException.class, () -> service.selectVideoResourceList(new VideoResource()));
        assertThrows(RuntimeException.class, () -> service.insertVideoResource(new VideoResource()));
        assertThrows(RuntimeException.class, () -> service.updateVideoResource(new VideoResource()));
        assertThrows(RuntimeException.class, () -> service.deleteVideoResourceByVideoIds(new Long[]{1L}));
        assertThrows(RuntimeException.class, () -> service.deleteVideoResourceByVideoId(1L));
    }
} 