package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.CourseImg;
import com.ruoyi.system.mapper.CourseImgMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * CourseImgServiceImpl 单元测试
 */
@ExtendWith(MockitoExtension.class)
class CourseImgServiceImplTest {

    @Mock
    private CourseImgMapper mapper;

    @InjectMocks
    private CourseImgServiceImpl service;

    private CourseImg img;

    @BeforeEach
    void setUp() {
        img = new CourseImg();
        img.setMapId(1L);
    }

    @Test
    void insert_shouldSetCreateTime() {
        when(mapper.insertCourseImg(any())).thenReturn(1);

        int res = service.insertCourseImg(img);

        assertEquals(1, res);
        assertNotNull(img.getCreateTime());
        verify(mapper).insertCourseImg(img);
    }

    @Test
    void update_shouldSetUpdateTime() {
        when(mapper.updateCourseImg(any())).thenReturn(1);

        int res = service.updateCourseImg(img);
        assertEquals(1, res);
        assertNotNull(img.getUpdateTime());
        verify(mapper).updateCourseImg(img);
    }

    @Test
    void select_and_delete_methods_delegate() {
        when(mapper.selectCourseImgByMapId(1L)).thenReturn(img);
        when(mapper.selectCourseImgList(any())).thenReturn(Collections.singletonList(img));
        when(mapper.deleteCourseImgByMapIds(any())).thenReturn(1);
        when(mapper.deleteCourseImgByMapId(1L)).thenReturn(1);

        assertNotNull(service.selectCourseImgByMapId(1L));
        assertEquals(1, service.selectCourseImgList(new CourseImg()).size());
        assertEquals(1, service.deleteCourseImgByMapIds(new Long[]{1L}));
        assertEquals(1, service.deleteCourseImgByMapId(1L));
    }

    @Test
    void selectMethods_mapperReturnsNull_shouldReturnNullOrEmpty() {
        when(mapper.selectCourseImgByMapId(2L)).thenReturn(null);
        when(mapper.selectCourseImgList(any())).thenReturn(null);

        assertNull(service.selectCourseImgByMapId(2L));
        List<CourseImg> list = service.selectCourseImgList(new CourseImg());
        assertNull(list);
    }

    @Test
    void deleteCourseImgByMapIds_emptyArray_shouldReturnZero() {
        when(mapper.deleteCourseImgByMapIds(any())).thenReturn(0);
        int res = service.deleteCourseImgByMapIds(new Long[0]);
        assertEquals(0, res);
    }

    @Test
    void mapperExceptions_shouldPropagate() {
        RuntimeException dbErr = new RuntimeException("DB error");
        when(mapper.insertCourseImg(any())).thenThrow(dbErr);
        when(mapper.updateCourseImg(any())).thenThrow(dbErr);
        when(mapper.selectCourseImgByMapId(anyLong())).thenThrow(dbErr);
        when(mapper.selectCourseImgList(any())).thenThrow(dbErr);
        when(mapper.deleteCourseImgByMapIds(any())).thenThrow(dbErr);
        when(mapper.deleteCourseImgByMapId(anyLong())).thenThrow(dbErr);

        assertThrows(RuntimeException.class, () -> service.insertCourseImg(img));
        assertThrows(RuntimeException.class, () -> service.updateCourseImg(img));
        assertThrows(RuntimeException.class, () -> service.selectCourseImgByMapId(1L));
        assertThrows(RuntimeException.class, () -> service.selectCourseImgList(new CourseImg()));
        assertThrows(RuntimeException.class, () -> service.deleteCourseImgByMapIds(new Long[]{1L}));
        assertThrows(RuntimeException.class, () -> service.deleteCourseImgByMapId(1L));
    }
} 