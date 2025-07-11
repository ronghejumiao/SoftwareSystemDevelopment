package com.ruoyi.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.system.domain.TaskSubmission;
import com.ruoyi.system.service.ITaskSubmissionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * TaskSubmissionController 的单元测试类
 *
 * @see com.ruoyi.web.controller.TaskSubmissionController
 */
@WebMvcTest(TaskSubmissionController.class)
@DisplayName("任务提交记录Controller测试")
class TaskSubmissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ITaskSubmissionService taskSubmissionService;

    //--- 模拟框架依赖 ---
    @MockBean
    private RedisCache redisCache;
    @MockBean
    private TokenService tokenService;

    private static final String MOCK_USER = "testUser";
    private static final String PERMISSION_PREFIX = "system:submission:";
    private static final Long STUDENT_USER_ID = 100L;

    // --- Role-Agnostic Tests (Tests that don't depend on user role) ---

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "query")
    @DisplayName("测试根据ID获取任务提交记录 - 成功")
    void testGetInfo_Success() throws Exception {
        long submissionId = 1L;
        TaskSubmission submission = new TaskSubmission();
        submission.setSubmissionId(submissionId);

        when(taskSubmissionService.selectTaskSubmissionBySubmissionId(submissionId)).thenReturn(submission);

        mockMvc.perform(get("/system/submission/{submissionId}", submissionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.submissionId").value(submissionId));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "add")
    @DisplayName("测试新增任务提交记录 - 成功")
    void testAdd_Success() throws Exception {
        TaskSubmission submission = new TaskSubmission();
        when(taskSubmissionService.insertTaskSubmission(any(TaskSubmission.class))).thenReturn(1);

        mockMvc.perform(post("/system/submission").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(submission)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "edit")
    @DisplayName("测试修改任务提交记录 - 成功")
    void testEdit_Success() throws Exception {
        TaskSubmission submission = new TaskSubmission();
        when(taskSubmissionService.updateTaskSubmission(any(TaskSubmission.class))).thenReturn(1);

        mockMvc.perform(put("/system/submission").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(submission)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "remove")
    @DisplayName("测试删除任务提交记录 - 成功")
    void testRemove_Success() throws Exception {
        Long[] ids = {1L, 2L};
        when(taskSubmissionService.deleteTaskSubmissionBySubmissionIds(ids)).thenReturn(1);

        mockMvc.perform(delete("/system/submission/{submissionIds}", "1,2").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));
    }

    // --- Role-Specific Tests ---

    @Nested
    @DisplayName("管理员或教师视角测试")
    class AdminOrTeacherTests {
        @Test
        @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "list")
        @DisplayName("查询任务提交列表")
        void testList_AsAdmin() throws Exception {
            try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
                mockedSecurity.when(SecurityUtils::isStudent).thenReturn(false);
                when(taskSubmissionService.selectTaskSubmissionList(any(TaskSubmission.class)))
                        .thenReturn(Collections.singletonList(new TaskSubmission()));

                mockMvc.perform(get("/system/submission/list"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.total").value(1));
            }
        }

        @Test
        @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "export")
        @DisplayName("导出任务提交列表")
        void testExport_AsAdmin() throws Exception {
            try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
                mockedSecurity.when(SecurityUtils::isStudent).thenReturn(false);

                mockMvc.perform(post("/system/submission/export").with(csrf()))
                        .andExpect(status().isOk());

                verify(taskSubmissionService).selectTaskSubmissionList(any(TaskSubmission.class));
            }
        }
    }

    @Nested
    @DisplayName("学生视角测试")
    class StudentTests {

        @Test
        @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "list")
        @DisplayName("查询自己的任务提交列表")
        void testList_AsStudent() throws Exception {
            ArgumentCaptor<TaskSubmission> captor = ArgumentCaptor.forClass(TaskSubmission.class);
            try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
                mockedSecurity.when(SecurityUtils::isStudent).thenReturn(true);
                mockedSecurity.when(SecurityUtils::getUserId).thenReturn(STUDENT_USER_ID);

                mockMvc.perform(get("/system/submission/list"))
                        .andExpect(status().isOk());

                verify(taskSubmissionService).selectTaskSubmissionList(captor.capture());
                assertEquals(STUDENT_USER_ID, captor.getValue().getParams().get("userId"));
            }
        }

        @Test
        @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "export")
        @DisplayName("导出自己的任务提交列表")
        void testExport_AsStudent() throws Exception {
            ArgumentCaptor<TaskSubmission> captor = ArgumentCaptor.forClass(TaskSubmission.class);
            try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
                mockedSecurity.when(SecurityUtils::isStudent).thenReturn(true);
                mockedSecurity.when(SecurityUtils::getUserId).thenReturn(STUDENT_USER_ID);

                mockMvc.perform(post("/system/submission/export").with(csrf()))
                        .andExpect(status().isOk());

                verify(taskSubmissionService).selectTaskSubmissionList(captor.capture());
                assertEquals(STUDENT_USER_ID, captor.getValue().getParams().get("userId"));
            }
        }
    }
}