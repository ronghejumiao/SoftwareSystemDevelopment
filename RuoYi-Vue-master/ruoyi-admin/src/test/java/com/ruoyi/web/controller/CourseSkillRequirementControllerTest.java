package com.ruoyi.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.system.domain.CourseSkillRequirement;
import com.ruoyi.system.service.ICourseSkillRequirementService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * CourseSkillRequirementController 的单元测试类
 *
 * @see com.ruoyi.web.controller.CourseSkillRequirementController
 */
@WebMvcTest(CourseSkillRequirementController.class)
@DisplayName("课程能力要求Controller测试")
class CourseSkillRequirementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ICourseSkillRequirementService courseSkillRequirementService;

    //--- 模拟框架依赖 ---
    @MockBean
    private RedisCache redisCache;
    @MockBean
    private TokenService tokenService;

    private static final String MOCK_USER = "testUser";
    private static final String PERMISSION_PREFIX = "system:requirement:";

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "list")
    @DisplayName("测试查询课程能力要求列表 - 成功")
    void testList_Success() throws Exception {
        when(courseSkillRequirementService.selectCourseSkillRequirementList(any(CourseSkillRequirement.class)))
                .thenReturn(Collections.singletonList(new CourseSkillRequirement()));

        mockMvc.perform(get("/system/requirement/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1));

        verify(courseSkillRequirementService).selectCourseSkillRequirementList(any(CourseSkillRequirement.class));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "export")
    @DisplayName("测试导出课程能力要求列表 - 成功")
    void testExport_Success() throws Exception {
        when(courseSkillRequirementService.selectCourseSkillRequirementList(any(CourseSkillRequirement.class)))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(post("/system/requirement/export").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8"));

        verify(courseSkillRequirementService).selectCourseSkillRequirementList(any(CourseSkillRequirement.class));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "query")
    @DisplayName("测试根据ID获取课程能力要求 - 成功")
    void testGetInfo_Success() throws Exception {
        long requirementId = 1L;
        CourseSkillRequirement requirement = new CourseSkillRequirement();
        requirement.setRequirementId(requirementId);

        when(courseSkillRequirementService.selectCourseSkillRequirementByRequirementId(requirementId)).thenReturn(requirement);

        mockMvc.perform(get("/system/requirement/{requirementId}", requirementId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS))
                .andExpect(jsonPath("$.data.requirementId").value(requirementId));

        verify(courseSkillRequirementService).selectCourseSkillRequirementByRequirementId(requirementId);
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "query")
    @DisplayName("测试根据ID获取课程能力要求 - 未找到")
    void testGetInfo_NotFound() throws Exception {
        long requirementId = 99L;
        when(courseSkillRequirementService.selectCourseSkillRequirementByRequirementId(requirementId)).thenReturn(null);

        mockMvc.perform(get("/system/requirement/{requirementId}", requirementId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(courseSkillRequirementService).selectCourseSkillRequirementByRequirementId(requirementId);
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "add")
    @DisplayName("测试新增课程能力要求 - 成功")
    void testAdd_Success() throws Exception {
        CourseSkillRequirement requirement = new CourseSkillRequirement();
        requirement.setCourseId(1L);

        when(courseSkillRequirementService.insertCourseSkillRequirement(any(CourseSkillRequirement.class))).thenReturn(1);

        mockMvc.perform(post("/system/requirement").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requirement)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));

        verify(courseSkillRequirementService).insertCourseSkillRequirement(any(CourseSkillRequirement.class));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "add")
    @DisplayName("测试新增课程能力要求 - 失败")
    void testAdd_Failure() throws Exception {
        CourseSkillRequirement requirement = new CourseSkillRequirement();
        when(courseSkillRequirementService.insertCourseSkillRequirement(any(CourseSkillRequirement.class))).thenReturn(0);

        mockMvc.perform(post("/system/requirement").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requirement)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.ERROR));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "edit")
    @DisplayName("测试修改课程能力要求 - 成功")
    void testEdit_Success() throws Exception {
        CourseSkillRequirement requirement = new CourseSkillRequirement();
        requirement.setRequirementId(1L);

        when(courseSkillRequirementService.updateCourseSkillRequirement(any(CourseSkillRequirement.class))).thenReturn(1);

        mockMvc.perform(put("/system/requirement").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requirement)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));

        verify(courseSkillRequirementService).updateCourseSkillRequirement(any(CourseSkillRequirement.class));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "edit")
    @DisplayName("测试修改课程能力要求 - 失败")
    void testEdit_Failure() throws Exception {
        CourseSkillRequirement requirement = new CourseSkillRequirement();
        requirement.setRequirementId(1L);
        when(courseSkillRequirementService.updateCourseSkillRequirement(any(CourseSkillRequirement.class))).thenReturn(0);

        mockMvc.perform(put("/system/requirement").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requirement)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.ERROR));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "remove")
    @DisplayName("测试删除课程能力要求 - 成功")
    void testRemove_Success() throws Exception {
        Long[] requirementIds = {1L, 2L};
        when(courseSkillRequirementService.deleteCourseSkillRequirementByRequirementIds(requirementIds)).thenReturn(requirementIds.length);

        mockMvc.perform(delete("/system/requirement/{requirementIds}", "1,2").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));

        verify(courseSkillRequirementService).deleteCourseSkillRequirementByRequirementIds(requirementIds);
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "remove")
    @DisplayName("测试删除课程能力要求 - 失败")
    void testRemove_Failure() throws Exception {
        Long[] requirementIds = {99L};
        when(courseSkillRequirementService.deleteCourseSkillRequirementByRequirementIds(requirementIds)).thenReturn(0);

        mockMvc.perform(delete("/system/requirement/{requirementIds}", "99").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.ERROR));
    }
}