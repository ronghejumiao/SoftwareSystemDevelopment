package com.ruoyi.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.system.domain.LearningTask;
import com.ruoyi.system.service.ILearningTaskService;
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
 * LearningTaskController 的单元测试类
 *
 * @see com.ruoyi.web.controller.LearningTaskController
 */
@WebMvcTest(LearningTaskController.class)
@DisplayName("学习任务Controller测试")
class LearningTaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ILearningTaskService learningTaskService;

    //--- 模拟框架依赖 ---
    @MockBean
    private RedisCache redisCache;
    @MockBean
    private TokenService tokenService;

    private static final String MOCK_USER = "testUser";
    private static final String PERMISSION_PREFIX = "system:task:";

    @Test
    @WithMockUser(username = MOCK_USER) // 注意：此接口没有权限注解
    @DisplayName("测试查询学习任务列表 - 成功")
    void testList_Success() throws Exception {
        when(learningTaskService.selectLearningTaskList(any(LearningTask.class)))
                .thenReturn(Collections.singletonList(new LearningTask()));

        mockMvc.perform(get("/system/task/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1));

        verify(learningTaskService).selectLearningTaskList(any(LearningTask.class));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "export")
    @DisplayName("测试导出学习任务列表 - 成功")
    void testExport_Success() throws Exception {
        when(learningTaskService.selectLearningTaskList(any(LearningTask.class)))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(post("/system/task/export").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8"));

        verify(learningTaskService).selectLearningTaskList(any(LearningTask.class));
    }

    @Test
    @WithMockUser(username = MOCK_USER) // 注意：此接口没有权限注解
    @DisplayName("测试根据ID获取学习任务 - 成功")
    void testGetInfo_Success() throws Exception {
        long taskId = 1L;
        LearningTask task = new LearningTask();
        task.setTaskId(taskId);

        when(learningTaskService.selectLearningTaskByTaskId(taskId)).thenReturn(task);

        mockMvc.perform(get("/system/task/{taskId}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS))
                .andExpect(jsonPath("$.data.taskId").value(taskId));

        verify(learningTaskService).selectLearningTaskByTaskId(taskId);
    }

    @Test
    @WithMockUser(username = MOCK_USER)
    @DisplayName("测试根据ID获取学习任务 - 未找到")
    void testGetInfo_NotFound() throws Exception {
        long taskId = 99L;
        when(learningTaskService.selectLearningTaskByTaskId(taskId)).thenReturn(null);

        mockMvc.perform(get("/system/task/{taskId}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(learningTaskService).selectLearningTaskByTaskId(taskId);
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "add")
    @DisplayName("测试新增学习任务 - 成功")
    void testAdd_Success() throws Exception {
        LearningTask task = new LearningTask();
        task.setTaskName("新任务");

        when(learningTaskService.insertLearningTask(any(LearningTask.class))).thenReturn(1);

        mockMvc.perform(post("/system/task").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));

        verify(learningTaskService).insertLearningTask(any(LearningTask.class));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "add")
    @DisplayName("测试新增学习任务 - 失败")
    void testAdd_Failure() throws Exception {
        LearningTask task = new LearningTask();
        when(learningTaskService.insertLearningTask(any(LearningTask.class))).thenReturn(0);

        mockMvc.perform(post("/system/task").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.ERROR));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "edit")
    @DisplayName("测试修改学习任务 - 成功")
    void testEdit_Success() throws Exception {
        LearningTask task = new LearningTask();
        task.setTaskId(1L);

        when(learningTaskService.updateLearningTask(any(LearningTask.class))).thenReturn(1);

        mockMvc.perform(put("/system/task").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));

        verify(learningTaskService).updateLearningTask(any(LearningTask.class));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "edit")
    @DisplayName("测试修改学习任务 - 失败")
    void testEdit_Failure() throws Exception {
        LearningTask task = new LearningTask();
        task.setTaskId(1L);
        when(learningTaskService.updateLearningTask(any(LearningTask.class))).thenReturn(0);

        mockMvc.perform(put("/system/task").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.ERROR));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "remove")
    @DisplayName("测试删除学习任务 - 成功")
    void testRemove_Success() throws Exception {
        Long[] taskIds = {1L, 2L};
        when(learningTaskService.deleteLearningTaskByTaskIds(taskIds)).thenReturn(taskIds.length);

        mockMvc.perform(delete("/system/task/{taskIds}", "1,2").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));

        verify(learningTaskService).deleteLearningTaskByTaskIds(taskIds);
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "remove")
    @DisplayName("测试删除学习任务 - 失败")
    void testRemove_Failure() throws Exception {
        Long[] taskIds = {99L};
        when(learningTaskService.deleteLearningTaskByTaskIds(taskIds)).thenReturn(0);

        mockMvc.perform(delete("/system/task/{taskIds}", "99").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.ERROR));
    }
}