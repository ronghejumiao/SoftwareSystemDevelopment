package com.ruoyi.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.system.domain.LearningRecord;
import com.ruoyi.system.service.ILearningRecordService;
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
 * LearningRecordController 的单元测试类
 *
 * @see com.ruoyi.web.controller.LearningRecordController
 */
@WebMvcTest(LearningRecordController.class)
@DisplayName("学习记录Controller测试")
class LearningRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ILearningRecordService learningRecordService;

    //--- 模拟框架依赖 ---
    @MockBean
    private RedisCache redisCache;
    @MockBean
    private TokenService tokenService;

    private static final String MOCK_USER = "testUser";
    private static final String PERMISSION_PREFIX = "system:record:";

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "list")
    @DisplayName("测试查询学习记录列表 - 成功")
    void testList_Success() throws Exception {
        when(learningRecordService.selectLearningRecordList(any(LearningRecord.class)))
                .thenReturn(Collections.singletonList(new LearningRecord()));

        mockMvc.perform(get("/system/record/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1));

        verify(learningRecordService).selectLearningRecordList(any(LearningRecord.class));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "export")
    @DisplayName("测试导出学习记录列表 - 成功")
    void testExport_Success() throws Exception {
        when(learningRecordService.selectLearningRecordList(any(LearningRecord.class)))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(post("/system/record/export").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8"));

        verify(learningRecordService).selectLearningRecordList(any(LearningRecord.class));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "query")
    @DisplayName("测试根据ID获取学习记录 - 成功")
    void testGetInfo_Success() throws Exception {
        long recordId = 1L;
        LearningRecord record = new LearningRecord();
        record.setRecordId(recordId);

        when(learningRecordService.selectLearningRecordByRecordId(recordId)).thenReturn(record);

        mockMvc.perform(get("/system/record/{recordId}", recordId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS))
                .andExpect(jsonPath("$.data.recordId").value(recordId));

        verify(learningRecordService).selectLearningRecordByRecordId(recordId);
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "query")
    @DisplayName("测试根据ID获取学习记录 - 未找到")
    void testGetInfo_NotFound() throws Exception {
        long recordId = 99L;
        when(learningRecordService.selectLearningRecordByRecordId(recordId)).thenReturn(null);

        mockMvc.perform(get("/system/record/{recordId}", recordId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(learningRecordService).selectLearningRecordByRecordId(recordId);
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "add")
    @DisplayName("测试新增学习记录 - 成功")
    void testAdd_Success() throws Exception {
        LearningRecord record = new LearningRecord();
        record.setUserId(1L);
        record.setCourseId(1L);

        when(learningRecordService.insertLearningRecord(any(LearningRecord.class))).thenReturn(1);

        mockMvc.perform(post("/system/record").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(record)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));

        verify(learningRecordService).insertLearningRecord(any(LearningRecord.class));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "add")
    @DisplayName("测试新增学习记录 - 失败")
    void testAdd_Failure() throws Exception {
        LearningRecord record = new LearningRecord();
        when(learningRecordService.insertLearningRecord(any(LearningRecord.class))).thenReturn(0);

        mockMvc.perform(post("/system/record").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(record)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.ERROR));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "edit")
    @DisplayName("测试修改学习记录 - 成功")
    void testEdit_Success() throws Exception {
        LearningRecord record = new LearningRecord();
        record.setRecordId(1L);

        when(learningRecordService.updateLearningRecord(any(LearningRecord.class))).thenReturn(1);

        mockMvc.perform(put("/system/record").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(record)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));

        verify(learningRecordService).updateLearningRecord(any(LearningRecord.class));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "edit")
    @DisplayName("测试修改学习记录 - 失败")
    void testEdit_Failure() throws Exception {
        LearningRecord record = new LearningRecord();
        record.setRecordId(1L);
        when(learningRecordService.updateLearningRecord(any(LearningRecord.class))).thenReturn(0);

        mockMvc.perform(put("/system/record").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(record)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.ERROR));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "remove")
    @DisplayName("测试删除学习记录 - 成功")
    void testRemove_Success() throws Exception {
        Long[] recordIds = {1L, 2L};
        when(learningRecordService.deleteLearningRecordByRecordIds(recordIds)).thenReturn(recordIds.length);

        mockMvc.perform(delete("/system/record/{recordIds}", "1,2").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));

        verify(learningRecordService).deleteLearningRecordByRecordIds(recordIds);
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "remove")
    @DisplayName("测试删除学习记录 - 失败")
    void testRemove_Failure() throws Exception {
        Long[] recordIds = {99L};
        when(learningRecordService.deleteLearningRecordByRecordIds(recordIds)).thenReturn(0);

        mockMvc.perform(delete("/system/record/{recordIds}", "99").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.ERROR));
    }
}