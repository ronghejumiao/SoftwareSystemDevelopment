package com.ruoyi.system.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.StudentClass;
import com.ruoyi.system.mapper.StudentClassMapper;

class StudentClassServiceImplTest {

    @Mock
    private StudentClassMapper studentClassMapper;

    @InjectMocks
    private StudentClassServiceImpl studentClassService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSelectStudentClassById() {
        Long id = 1L;
        StudentClass expected = new StudentClass();
        expected.setId(id);

        when(studentClassMapper.selectStudentClassById(id)).thenReturn(expected);

        StudentClass result = studentClassService.selectStudentClassById(id);

        assertEquals(expected, result);
        verify(studentClassMapper).selectStudentClassById(id);
    }

    @Test
    void testSelectStudentClassList() {
        StudentClass query = new StudentClass();
        List<StudentClass> expectedList = Arrays.asList(new StudentClass(), new StudentClass());

        when(studentClassMapper.selectStudentClassList(query)).thenReturn(expectedList);

        List<StudentClass> result = studentClassService.selectStudentClassList(query);

        assertEquals(expectedList, result);
        verify(studentClassMapper).selectStudentClassList(query);
    }

    @Test
    void testInsertStudentClass() {
        StudentClass studentClass = new StudentClass();
        studentClass.setStudentId(1L);
        studentClass.setClassId(1L);

        when(studentClassMapper.insertStudentClass(studentClass)).thenReturn(1);

        int result = studentClassService.insertStudentClass(studentClass);

        assertEquals(1, result);
        assertNotNull(studentClass.getCreateTime());
        verify(studentClassMapper).insertStudentClass(studentClass);
    }

    @Test
    void testUpdateStudentClass() {
        StudentClass studentClass = new StudentClass();
        studentClass.setId(1L);

        when(studentClassMapper.updateStudentClass(studentClass)).thenReturn(1);

        int result = studentClassService.updateStudentClass(studentClass);

        assertEquals(1, result);
        assertNotNull(studentClass.getUpdateTime());
        verify(studentClassMapper).updateStudentClass(studentClass);
    }

    @Test
    void testDeleteStudentClassByIds() {
        Long[] ids = {1L, 2L};

        when(studentClassMapper.deleteStudentClassByIds(ids)).thenReturn(2);

        int result = studentClassService.deleteStudentClassByIds(ids);

        assertEquals(2, result);
        verify(studentClassMapper).deleteStudentClassByIds(ids);
    }

    @Test
    void testSelectStudentClassById_mapperReturnsNull() {
        when(studentClassMapper.selectStudentClassById(99L)).thenReturn(null);
        assertNull(studentClassService.selectStudentClassById(99L));
    }

    @Test
    void testSelectStudentClassList_mapperReturnsNull() {
        when(studentClassMapper.selectStudentClassList(any())).thenReturn(null);
        assertNull(studentClassService.selectStudentClassList(new StudentClass()));
    }

    @Test
    void testDeleteStudentClassByIds_emptyArray() {
        when(studentClassMapper.deleteStudentClassByIds(new Long[0])).thenReturn(0);
        assertEquals(0, studentClassService.deleteStudentClassByIds(new Long[0]));
    }

    @Test
    void allMapperMethods_throwException_shouldPropagate() {
        RuntimeException ex = new RuntimeException("db error");
        when(studentClassMapper.selectStudentClassById(anyLong())).thenThrow(ex);
        when(studentClassMapper.selectStudentClassList(any())).thenThrow(ex);
        when(studentClassMapper.insertStudentClass(any())).thenThrow(ex);
        when(studentClassMapper.updateStudentClass(any())).thenThrow(ex);
        when(studentClassMapper.deleteStudentClassByIds(any())).thenThrow(ex);
        when(studentClassMapper.deleteStudentClassById(anyLong())).thenThrow(ex);
        assertThrows(RuntimeException.class, () -> studentClassService.selectStudentClassById(1L));
        assertThrows(RuntimeException.class, () -> studentClassService.selectStudentClassList(new StudentClass()));
        assertThrows(RuntimeException.class, () -> studentClassService.insertStudentClass(new StudentClass()));
        assertThrows(RuntimeException.class, () -> studentClassService.updateStudentClass(new StudentClass()));
        assertThrows(RuntimeException.class, () -> studentClassService.deleteStudentClassByIds(new Long[]{1L}));
        assertThrows(RuntimeException.class, () -> studentClassService.deleteStudentClassById(1L));
    }
}