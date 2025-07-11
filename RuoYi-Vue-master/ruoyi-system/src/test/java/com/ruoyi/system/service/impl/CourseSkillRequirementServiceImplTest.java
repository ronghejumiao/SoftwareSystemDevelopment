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
import com.ruoyi.system.domain.CourseSkillRequirement;
import com.ruoyi.system.mapper.CourseSkillRequirementMapper;

class CourseSkillRequirementServiceImplTest {

    @Mock
    private CourseSkillRequirementMapper courseSkillRequirementMapper;

    @InjectMocks
    private CourseSkillRequirementServiceImpl courseSkillRequirementService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSelectCourseSkillRequirementByRequirementId() {
        Long requirementId = 1L;
        CourseSkillRequirement expectedRequirement = new CourseSkillRequirement();
        expectedRequirement.setRequirementId(requirementId);

        when(courseSkillRequirementMapper.selectCourseSkillRequirementByRequirementId(requirementId))
                .thenReturn(expectedRequirement);

        CourseSkillRequirement result = courseSkillRequirementService.selectCourseSkillRequirementByRequirementId(requirementId);

        assertEquals(expectedRequirement, result);
        verify(courseSkillRequirementMapper).selectCourseSkillRequirementByRequirementId(requirementId);
    }

    @Test
    void testSelectCourseSkillRequirementList() {
        CourseSkillRequirement query = new CourseSkillRequirement();
        List<CourseSkillRequirement> expectedList = Arrays.asList(new CourseSkillRequirement(), new CourseSkillRequirement());

        when(courseSkillRequirementMapper.selectCourseSkillRequirementList(query)).thenReturn(expectedList);

        List<CourseSkillRequirement> result = courseSkillRequirementService.selectCourseSkillRequirementList(query);

        assertEquals(expectedList, result);
        verify(courseSkillRequirementMapper).selectCourseSkillRequirementList(query);
    }

    @Test
    void testInsertCourseSkillRequirement() {
        CourseSkillRequirement requirement = new CourseSkillRequirement();
        requirement.setSkillName("Test Skill");

        when(courseSkillRequirementMapper.insertCourseSkillRequirement(requirement)).thenReturn(1);

        int result = courseSkillRequirementService.insertCourseSkillRequirement(requirement);

        assertEquals(1, result);
        assertNotNull(requirement.getCreateTime());
        verify(courseSkillRequirementMapper).insertCourseSkillRequirement(requirement);
    }

    @Test
    void testUpdateCourseSkillRequirement() {
        CourseSkillRequirement requirement = new CourseSkillRequirement();
        requirement.setRequirementId(1L);
        requirement.setSkillName("Updated Skill");

        when(courseSkillRequirementMapper.updateCourseSkillRequirement(requirement)).thenReturn(1);

        int result = courseSkillRequirementService.updateCourseSkillRequirement(requirement);

        assertEquals(1, result);
        assertNotNull(requirement.getUpdateTime());
        verify(courseSkillRequirementMapper).updateCourseSkillRequirement(requirement);
    }

    @Test
    void testDeleteCourseSkillRequirementByRequirementIds() {
        Long[] requirementIds = {1L, 2L};

        when(courseSkillRequirementMapper.deleteCourseSkillRequirementByRequirementIds(requirementIds)).thenReturn(2);

        int result = courseSkillRequirementService.deleteCourseSkillRequirementByRequirementIds(requirementIds);

        assertEquals(2, result);
        verify(courseSkillRequirementMapper).deleteCourseSkillRequirementByRequirementIds(requirementIds);
    }

    @Test
    void selectMethods_mapperReturnsNull_shouldReturnNull() {
        when(courseSkillRequirementMapper.selectCourseSkillRequirementByRequirementId(99L)).thenReturn(null);
        when(courseSkillRequirementMapper.selectCourseSkillRequirementList(any())).thenReturn(null);
        assertNull(courseSkillRequirementService.selectCourseSkillRequirementByRequirementId(99L));
        assertNull(courseSkillRequirementService.selectCourseSkillRequirementList(new CourseSkillRequirement()));
    }

    @Test
    void deleteById_delegate() {
        when(courseSkillRequirementMapper.deleteCourseSkillRequirementByRequirementId(1L)).thenReturn(1);
        assertEquals(1, courseSkillRequirementService.deleteCourseSkillRequirementByRequirementId(1L));
        verify(courseSkillRequirementMapper).deleteCourseSkillRequirementByRequirementId(1L);
    }

    @Test
    void deleteByIds_empty_shouldReturnZero() {
        when(courseSkillRequirementMapper.deleteCourseSkillRequirementByRequirementIds(any())).thenReturn(0);
        assertEquals(0, courseSkillRequirementService.deleteCourseSkillRequirementByRequirementIds(new Long[0]));
    }

    @Test
    void mapperExceptions_shouldPropagate() {
        RuntimeException dbErr = new RuntimeException("DB error");
        when(courseSkillRequirementMapper.insertCourseSkillRequirement(any())).thenThrow(dbErr);
        when(courseSkillRequirementMapper.updateCourseSkillRequirement(any())).thenThrow(dbErr);
        when(courseSkillRequirementMapper.selectCourseSkillRequirementByRequirementId(anyLong())).thenThrow(dbErr);
        when(courseSkillRequirementMapper.selectCourseSkillRequirementList(any())).thenThrow(dbErr);
        when(courseSkillRequirementMapper.deleteCourseSkillRequirementByRequirementIds(any())).thenThrow(dbErr);
        when(courseSkillRequirementMapper.deleteCourseSkillRequirementByRequirementId(anyLong())).thenThrow(dbErr);

        CourseSkillRequirement req = new CourseSkillRequirement();
        assertThrows(RuntimeException.class, () -> courseSkillRequirementService.insertCourseSkillRequirement(req));
        assertThrows(RuntimeException.class, () -> courseSkillRequirementService.updateCourseSkillRequirement(req));
        assertThrows(RuntimeException.class, () -> courseSkillRequirementService.selectCourseSkillRequirementByRequirementId(1L));
        assertThrows(RuntimeException.class, () -> courseSkillRequirementService.selectCourseSkillRequirementList(req));
        assertThrows(RuntimeException.class, () -> courseSkillRequirementService.deleteCourseSkillRequirementByRequirementIds(new Long[]{1L}));
        assertThrows(RuntimeException.class, () -> courseSkillRequirementService.deleteCourseSkillRequirementByRequirementId(1L));
    }
}