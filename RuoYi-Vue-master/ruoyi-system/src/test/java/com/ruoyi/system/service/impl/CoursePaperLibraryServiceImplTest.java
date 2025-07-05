package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.CoursePaperLibrary;
import com.ruoyi.system.mapper.CoursePaperLibraryMapper;
import org.junit.jupiter.api.BeforeEach;
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
 * CoursePaperLibraryServiceImpl 单元测试
 */
@ExtendWith(MockitoExtension.class)
class CoursePaperLibraryServiceImplTest {

    @Mock
    private CoursePaperLibraryMapper mapper;

    @InjectMocks
    private CoursePaperLibraryServiceImpl service;

    private CoursePaperLibrary library;

    @BeforeEach
    void setUp() {
        library = new CoursePaperLibrary();
        library.setLibraryId(100L);
        library.setCourseId(5L);
    }

    @Test
    void insert_setsCreateTime() {
        when(mapper.insertCoursePaperLibrary(any())).thenReturn(1);
        assertEquals(1, service.insertCoursePaperLibrary(library));
        assertNotNull(library.getCreateTime());
    }

    @Test
    void update_setsUpdateTime() {
        when(mapper.updateCoursePaperLibrary(any())).thenReturn(1);
        assertEquals(1, service.updateCoursePaperLibrary(library));
        assertNotNull(library.getUpdateTime());
    }

    @Test
    void queryAndDelete_delegate() {
        when(mapper.selectCoursePaperLibraryByLibraryId(100L)).thenReturn(library);
        when(mapper.selectCoursePaperLibraryList(any())).thenReturn(Collections.singletonList(library));
        when(mapper.deleteCoursePaperLibraryByLibraryIds(any())).thenReturn(2);
        when(mapper.deleteCoursePaperLibraryByLibraryId(100L)).thenReturn(1);
        when(mapper.selectCoursePaperLibraryByCourseId(5L)).thenReturn(library);

        assertEquals(library, service.selectCoursePaperLibraryByLibraryId(100L));
        assertEquals(1, service.selectCoursePaperLibraryList(new CoursePaperLibrary()).size());
        assertEquals(2, service.deleteCoursePaperLibraryByLibraryIds(new Long[]{100L, 101L}));
        assertEquals(1, service.deleteCoursePaperLibraryByLibraryId(100L));
        assertEquals(library, service.selectCoursePaperLibraryByCourseId(5L));
    }

    @Test
    void selectMethods_mapperReturnsNull_shouldReturnNullOrEmpty() {
        when(mapper.selectCoursePaperLibraryByLibraryId(200L)).thenReturn(null);
        when(mapper.selectCoursePaperLibraryList(any())).thenReturn(null);
        when(mapper.selectCoursePaperLibraryByCourseId(10L)).thenReturn(null);

        assertNull(service.selectCoursePaperLibraryByLibraryId(200L));
        assertNull(service.selectCoursePaperLibraryList(new CoursePaperLibrary()));
        assertNull(service.selectCoursePaperLibraryByCourseId(10L));
    }

    @Test
    void deleteCoursePaperLibraryByLibraryIds_emptyArray_shouldReturnZero() {
        when(mapper.deleteCoursePaperLibraryByLibraryIds(any())).thenReturn(0);
        assertEquals(0, service.deleteCoursePaperLibraryByLibraryIds(new Long[0]));
    }

    @Test
    void mapperExceptions_shouldPropagate() {
        RuntimeException dbErr = new RuntimeException("DB error");
        when(mapper.insertCoursePaperLibrary(any())).thenThrow(dbErr);
        when(mapper.updateCoursePaperLibrary(any())).thenThrow(dbErr);
        when(mapper.selectCoursePaperLibraryByLibraryId(anyLong())).thenThrow(dbErr);
        when(mapper.selectCoursePaperLibraryList(any())).thenThrow(dbErr);
        when(mapper.deleteCoursePaperLibraryByLibraryIds(any())).thenThrow(dbErr);
        when(mapper.deleteCoursePaperLibraryByLibraryId(anyLong())).thenThrow(dbErr);
        when(mapper.selectCoursePaperLibraryByCourseId(anyLong())).thenThrow(dbErr);

        assertThrows(RuntimeException.class, () -> service.insertCoursePaperLibrary(library));
        assertThrows(RuntimeException.class, () -> service.updateCoursePaperLibrary(library));
        assertThrows(RuntimeException.class, () -> service.selectCoursePaperLibraryByLibraryId(1L));
        assertThrows(RuntimeException.class, () -> service.selectCoursePaperLibraryList(new CoursePaperLibrary()));
        assertThrows(RuntimeException.class, () -> service.deleteCoursePaperLibraryByLibraryIds(new Long[]{1L}));
        assertThrows(RuntimeException.class, () -> service.deleteCoursePaperLibraryByLibraryId(1L));
        assertThrows(RuntimeException.class, () -> service.selectCoursePaperLibraryByCourseId(1L));
    }
} 