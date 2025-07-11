package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.LearningResource;
import com.ruoyi.system.mapper.LearningResourceMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * LearningResourceServiceImpl 单元测试
 */
@ExtendWith(MockitoExtension.class)
class LearningResourceServiceImplTest {

    @Mock
    private LearningResourceMapper mapper;
    @InjectMocks
    private LearningResourceServiceImpl service;

    private LearningResource res;

    @BeforeEach
    void init() {
        res = new LearningResource();
        res.setResourceId(1L);
        res.setUploaderId(9L);
    }

    @Test
    void insert_whenUploaderProvided_shouldNotCallSecurityUtils() {
        when(mapper.insertLearningResource(any())).thenReturn(1);
        int rows = service.insertLearningResource(res);
        assertEquals(1, rows);
        verify(mapper).insertLearningResource(res);
    }

    @Test
    void insert_whenUploaderMissing_shouldFallbackToCurrentUser() {
        res.setUploaderId(null);
        when(mapper.insertLearningResource(any())).thenReturn(1);
        try (MockedStatic<com.ruoyi.common.utils.SecurityUtils> util = mockStatic(com.ruoyi.common.utils.SecurityUtils.class)) {
            util.when(com.ruoyi.common.utils.SecurityUtils::getUserId).thenReturn(5L);
            int rows = service.insertLearningResource(res);
            assertEquals(1, rows);
            assertEquals(5L, res.getUploaderId());
        }
    }

    @Test
    void insert_whenUploaderMissingAndSecurityUtilsThrows_shouldSetUploaderIdZero() {
        res.setUploaderId(null);
        when(mapper.insertLearningResource(any())).thenReturn(1);
        try (MockedStatic<com.ruoyi.common.utils.SecurityUtils> util = mockStatic(com.ruoyi.common.utils.SecurityUtils.class)) {
            util.when(com.ruoyi.common.utils.SecurityUtils::getUserId).thenThrow(new RuntimeException("no user"));
            int rows = service.insertLearningResource(res);
            assertEquals(1, rows);
            assertEquals(0L, res.getUploaderId());
        }
    }

    @Test
    void update_shouldDelegateToMapper() {
        when(mapper.updateLearningResource(any())).thenReturn(1);
        assertEquals(1, service.updateLearningResource(res));
        verify(mapper).updateLearningResource(res);
    }

    @Test
    void selectList_mapperReturnsNull_shouldReturnNull() {
        when(mapper.selectLearningResourceList(any())).thenReturn(null);
        assertNull(service.selectLearningResourceList(new LearningResource()));
    }

    @Test
    void deleteByResourceIds_emptyArray_shouldDelegateAndReturnZero() {
        when(mapper.deleteLearningResourceByResourceIds(any())).thenReturn(0);
        assertEquals(0, service.deleteLearningResourceByResourceIds(new Long[0]));
    }

    @Test
    void allMapperMethods_throwException_shouldPropagate() {
        RuntimeException ex = new RuntimeException("db error");
        when(mapper.selectLearningResourceByResourceId(anyLong())).thenThrow(ex);
        when(mapper.selectLearningResourceList(any())).thenThrow(ex);
        when(mapper.insertLearningResource(any())).thenThrow(ex);
        when(mapper.updateLearningResource(any())).thenThrow(ex);
        when(mapper.deleteLearningResourceByResourceIds(any())).thenThrow(ex);
        when(mapper.deleteLearningResourceByResourceId(anyLong())).thenThrow(ex);
        assertThrows(RuntimeException.class, () -> service.selectLearningResourceByResourceId(1L));
        assertThrows(RuntimeException.class, () -> service.selectLearningResourceList(new LearningResource()));
        assertThrows(RuntimeException.class, () -> service.insertLearningResource(res));
        assertThrows(RuntimeException.class, () -> service.updateLearningResource(res));
        assertThrows(RuntimeException.class, () -> service.deleteLearningResourceByResourceIds(new Long[]{1L}));
        assertThrows(RuntimeException.class, () -> service.deleteLearningResourceByResourceId(1L));
    }

    @Test
    void selectAndDelete_delegateToMapper() {
        when(mapper.selectLearningResourceByResourceId(1L)).thenReturn(res);
        when(mapper.selectLearningResourceList(any())).thenReturn(Collections.singletonList(res));
        when(mapper.deleteLearningResourceByResourceIds(any())).thenReturn(2);
        when(mapper.deleteLearningResourceByResourceId(1L)).thenReturn(1);

        assertEquals(res, service.selectLearningResourceByResourceId(1L));
        List<LearningResource> list = service.selectLearningResourceList(new LearningResource());
        assertEquals(1, list.size());
        assertEquals(2, service.deleteLearningResourceByResourceIds(new Long[]{1L,2L}));
        assertEquals(1, service.deleteLearningResourceByResourceId(1L));
    }
} 