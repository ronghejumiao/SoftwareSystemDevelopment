package com.ruoyi.system.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.CourseHomework;
import com.ruoyi.system.mapper.CourseHomeworkMapper;

class CourseHomeworkServiceImplTest {

    @Mock
    private CourseHomeworkMapper courseHomeworkMapper;

    @InjectMocks
    private CourseHomeworkServiceImpl courseHomeworkService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSelectCourseHomeworkByHomeworkId() {
        Long homeworkId = 1L;
        CourseHomework expectedHomework = new CourseHomework();
        expectedHomework.setHomeworkId(homeworkId);

        when(courseHomeworkMapper.selectCourseHomeworkByHomeworkId(homeworkId)).thenReturn(expectedHomework);

        CourseHomework result = courseHomeworkService.selectCourseHomeworkByHomeworkId(homeworkId);

        assertEquals(expectedHomework, result);
        verify(courseHomeworkMapper).selectCourseHomeworkByHomeworkId(homeworkId);
    }

    @Test
    void testSelectCourseHomeworkList() {
        CourseHomework query = new CourseHomework();
        List<CourseHomework> expectedList = Arrays.asList(new CourseHomework(), new CourseHomework());

        when(courseHomeworkMapper.selectCourseHomeworkList(query)).thenReturn(expectedList);

        List<CourseHomework> result = courseHomeworkService.selectCourseHomeworkList(query);

        assertEquals(expectedList, result);
        verify(courseHomeworkMapper).selectCourseHomeworkList(query);
    }


    @Test
    void testSelectCourseHomeworkListByIds() {
        List<Long> homeworkIds = Arrays.asList(1L, 2L);
        List<CourseHomework> expectedList = Arrays.asList(new CourseHomework(), new CourseHomework());

        when(courseHomeworkMapper.selectCourseHomeworkListByIds(homeworkIds)).thenReturn(expectedList);

        List<CourseHomework> result = courseHomeworkService.selectCourseHomeworkListByIds(homeworkIds);

        assertEquals(expectedList, result);
        verify(courseHomeworkMapper).selectCourseHomeworkListByIds(homeworkIds);
    }

    @Test
    void selectCourseHomeworkListByIds_nullOrEmpty_shouldReturnEmptyAndSkipMapper() {
        // null
        List<CourseHomework> listNull = courseHomeworkService.selectCourseHomeworkListByIds(null);
        assertNotNull(listNull);
        assertTrue(listNull.isEmpty());
        verify(courseHomeworkMapper, never()).selectCourseHomeworkListByIds(any());

        // empty
        List<CourseHomework> listEmpty = courseHomeworkService.selectCourseHomeworkListByIds(Collections.emptyList());
        assertNotNull(listEmpty);
        assertTrue(listEmpty.isEmpty());
        verify(courseHomeworkMapper, never()).selectCourseHomeworkListByIds(Collections.emptyList());
    }

    @Test
    void testSelectCourseHomeworkListByCourseId_mapperReturnsNull_shouldReturnNull() {
        when(courseHomeworkMapper.selectCourseHomeworkList(any())).thenReturn(null);
        List<CourseHomework> list = courseHomeworkService.selectCourseHomeworkListByCourseId(1L);
        assertNull(list);
    }

    @Test
    void testInsertCourseHomework() {
        CourseHomework homework = new CourseHomework();
        homework.setHomeworkName("Test Homework");

        when(courseHomeworkMapper.insertCourseHomework(homework)).thenReturn(1);

        int result = courseHomeworkService.insertCourseHomework(homework);

        assertEquals(1, result);
        assertNotNull(homework.getCreateTime());
        verify(courseHomeworkMapper).insertCourseHomework(homework);
    }

    @Test
    void testUpdateCourseHomework() {
        CourseHomework homework = new CourseHomework();
        homework.setHomeworkId(1L);
        homework.setHomeworkName("Updated Homework");

        when(courseHomeworkMapper.updateCourseHomework(homework)).thenReturn(1);

        int result = courseHomeworkService.updateCourseHomework(homework);

        assertEquals(1, result);
        assertNotNull(homework.getUpdateTime());
        verify(courseHomeworkMapper).updateCourseHomework(homework);
    }

    @Test
    void mapperExceptions_shouldPropagate() {
        RuntimeException dbErr = new RuntimeException("DB error");
        when(courseHomeworkMapper.insertCourseHomework(any())).thenThrow(dbErr);
        when(courseHomeworkMapper.updateCourseHomework(any())).thenThrow(dbErr);
        when(courseHomeworkMapper.selectCourseHomeworkList(any())).thenThrow(dbErr);
        when(courseHomeworkMapper.selectCourseHomeworkByHomeworkId(anyLong())).thenThrow(dbErr);
        when(courseHomeworkMapper.deleteCourseHomeworkByHomeworkIds(any())).thenThrow(dbErr);
        when(courseHomeworkMapper.deleteCourseHomeworkByHomeworkId(anyLong())).thenThrow(dbErr);

        CourseHomework hw = new CourseHomework();
        assertThrows(RuntimeException.class, () -> courseHomeworkService.insertCourseHomework(hw));
        assertThrows(RuntimeException.class, () -> courseHomeworkService.updateCourseHomework(hw));
        assertThrows(RuntimeException.class, () -> courseHomeworkService.selectCourseHomeworkList(new CourseHomework()));
        assertThrows(RuntimeException.class, () -> courseHomeworkService.selectCourseHomeworkByHomeworkId(1L));
        assertThrows(RuntimeException.class, () -> courseHomeworkService.deleteCourseHomeworkByHomeworkIds(new Long[]{1L}));
        assertThrows(RuntimeException.class, () -> courseHomeworkService.deleteCourseHomeworkByHomeworkId(1L));
    }
}