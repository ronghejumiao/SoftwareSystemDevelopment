package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.Course;
import com.ruoyi.system.mapper.CourseMapper;
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
 * CourseServiceImpl 单元测试
 */
@ExtendWith(MockitoExtension.class)
class CourseServiceImplTest {

    @Mock
    private CourseMapper mapper;

    @InjectMocks
    private CourseServiceImpl service;

    private Course course;

    @BeforeEach
    void setUp() {
        course = new Course();
        course.setCourseId(1L);
        course.setCourseName("测试课程");
    }

    @Test
    void insert_setsCreateTime() {
        when(mapper.insertCourse(any())).thenReturn(1);
        assertEquals(1, service.insertCourse(course));
        assertNotNull(course.getCreateTime());
        verify(mapper).insertCourse(course);
    }

    @Test
    void update_setsUpdateTime() {
        when(mapper.updateCourse(any())).thenReturn(1);
        assertEquals(1, service.updateCourse(course));
        assertNotNull(course.getUpdateTime());
        verify(mapper).updateCourse(course);
    }

    @Test
    void select_and_delete_delegate() {
        when(mapper.selectCourseByCourseId(1L)).thenReturn(course);
        when(mapper.selectCourseList(any())).thenReturn(Collections.singletonList(course));
        when(mapper.deleteCourseByCourseIds(any())).thenReturn(2);
        when(mapper.deleteCourseByCourseId(1L)).thenReturn(1);

        assertEquals(course, service.selectCourseByCourseId(1L));
        assertEquals(1, service.selectCourseList(new Course()).size());
        assertEquals(2, service.deleteCourseByCourseIds(new Long[]{1L,2L}));
        assertEquals(1, service.deleteCourseByCourseId(1L));
    }

    @Test
    void selectMethods_mapperReturnsNull_shouldReturnNull() {
        when(mapper.selectCourseByCourseId(2L)).thenReturn(null);
        when(mapper.selectCourseList(any())).thenReturn(null);
        assertNull(service.selectCourseByCourseId(2L));
        assertNull(service.selectCourseList(new Course()));
    }

    @Test
    void deleteCourseByCourseIds_emptyArray_shouldReturnZero() {
        when(mapper.deleteCourseByCourseIds(any())).thenReturn(0);
        assertEquals(0, service.deleteCourseByCourseIds(new Long[0]));
    }

    @Test
    void mapperExceptions_shouldPropagate() {
        RuntimeException dbErr = new RuntimeException("DB error");
        when(mapper.insertCourse(any())).thenThrow(dbErr);
        when(mapper.updateCourse(any())).thenThrow(dbErr);
        when(mapper.selectCourseByCourseId(anyLong())).thenThrow(dbErr);
        when(mapper.selectCourseList(any())).thenThrow(dbErr);
        when(mapper.deleteCourseByCourseIds(any())).thenThrow(dbErr);
        when(mapper.deleteCourseByCourseId(anyLong())).thenThrow(dbErr);

        assertThrows(RuntimeException.class, () -> service.insertCourse(course));
        assertThrows(RuntimeException.class, () -> service.updateCourse(course));
        assertThrows(RuntimeException.class, () -> service.selectCourseByCourseId(1L));
        assertThrows(RuntimeException.class, () -> service.selectCourseList(new Course()));
        assertThrows(RuntimeException.class, () -> service.deleteCourseByCourseIds(new Long[]{1L}));
        assertThrows(RuntimeException.class, () -> service.deleteCourseByCourseId(1L));
    }
} 