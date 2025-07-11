package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.StudentSkill;
import com.ruoyi.system.domain.CourseSkillRequirement;
import com.ruoyi.system.mapper.StudentSkillMapper;
import com.ruoyi.system.mapper.CourseSkillRequirementMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * StudentSkillServiceImpl 单元测试
 */
@ExtendWith(MockitoExtension.class)
class StudentSkillServiceImplTest {

    @Mock private StudentSkillMapper skillMapper;
    @Mock private CourseSkillRequirementMapper reqMapper;
    @InjectMocks private StudentSkillServiceImpl service;

    private StudentSkill skill;

    @BeforeEach
    void setUp() {
        skill = new StudentSkill();
        skill.setId(1L);
        skill.setRequirementId(11L);
        skill.setStudentId(2L);
    }

    @Test
    void update_setsUpdateTime() {
        when(skillMapper.updateStudentSkill(any())).thenReturn(1);
        assertEquals(1, service.updateStudentSkill(skill));
        assertNotNull(skill.getUpdateTime());
    }

    @Test
    void initStudentCourseSkills_requirementsEmpty_returnsZero() {
        when(reqMapper.selectCourseSkillRequirementList(any())).thenReturn(Collections.emptyList());
        int created = service.initStudentCourseSkills(2L, 3L);
        assertEquals(0, created);
    }

    @Test
    void initStudentCourseSkills_missingSkills_shouldInsert() {
        CourseSkillRequirement req1 = new CourseSkillRequirement();
        req1.setRequirementId(11L);
        CourseSkillRequirement req2 = new CourseSkillRequirement();
        req2.setRequirementId(12L);
        when(reqMapper.selectCourseSkillRequirementList(any())).thenReturn(Arrays.asList(req1, req2));
        when(skillMapper.selectStudentSkillByStudentAndCourse(2L,3L)).thenReturn(Collections.singletonList(skill)); // 已有11L,缺12L
        when(skillMapper.insertStudentSkill(any())).thenReturn(1);

        int created = service.initStudentCourseSkills(2L,3L);
        assertEquals(1, created);

        ArgumentCaptor<StudentSkill> captor = ArgumentCaptor.forClass(StudentSkill.class);
        verify(skillMapper).insertStudentSkill(captor.capture());
        assertEquals(12L, captor.getValue().getRequirementId());
    }

    @Test
    void selectById_mapperReturnsNull_shouldReturnNull() {
        when(skillMapper.selectStudentSkillById(99L)).thenReturn(null);
        assertNull(service.selectStudentSkillById(99L));
    }

    @Test
    void selectList_mapperReturnsNull_shouldReturnNull() {
        when(skillMapper.selectStudentSkillList(any())).thenReturn(null);
        assertNull(service.selectStudentSkillList(new StudentSkill()));
    }

    @Test
    void selectStudentSkillByStudentAndCourse_mapperReturnsNull_shouldReturnNull() {
        when(skillMapper.selectStudentSkillByStudentAndCourse(2L,3L)).thenReturn(null);
        assertNull(service.selectStudentSkillByStudentAndCourse(2L,3L));
    }

    @Test
    void deleteByIds_emptyArray_shouldReturnZero() {
        when(skillMapper.deleteStudentSkillByIds(new Long[0])).thenReturn(0);
        assertEquals(0, service.deleteStudentSkillByIds(new Long[0]));
    }

    @Test
    void allMapperMethods_throwException_shouldPropagate() {
        RuntimeException ex = new RuntimeException("db error");
        when(skillMapper.selectStudentSkillById(anyLong())).thenThrow(ex);
        when(skillMapper.selectStudentSkillList(any())).thenThrow(ex);
        when(skillMapper.insertStudentSkill(any())).thenThrow(ex);
        when(skillMapper.updateStudentSkill(any())).thenThrow(ex);
        when(skillMapper.deleteStudentSkillByIds(any())).thenThrow(ex);
        when(skillMapper.deleteStudentSkillById(anyLong())).thenThrow(ex);
        when(skillMapper.selectStudentSkillByStudentAndCourse(anyLong(), anyLong())).thenThrow(ex);
        when(reqMapper.selectCourseSkillRequirementList(any())).thenThrow(ex);
        assertThrows(RuntimeException.class, () -> service.selectStudentSkillById(1L));
        assertThrows(RuntimeException.class, () -> service.selectStudentSkillList(new StudentSkill()));
        assertThrows(RuntimeException.class, () -> service.insertStudentSkill(skill));
        assertThrows(RuntimeException.class, () -> service.updateStudentSkill(skill));
        assertThrows(RuntimeException.class, () -> service.deleteStudentSkillByIds(new Long[]{1L}));
        assertThrows(RuntimeException.class, () -> service.deleteStudentSkillById(1L));
        assertThrows(RuntimeException.class, () -> service.selectStudentSkillByStudentAndCourse(1L,2L));
        assertThrows(RuntimeException.class, () -> service.initStudentCourseSkills(1L,2L));
    }
} 