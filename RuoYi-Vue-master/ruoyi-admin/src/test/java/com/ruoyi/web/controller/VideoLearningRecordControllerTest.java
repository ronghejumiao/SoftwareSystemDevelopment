package com.ruoyi.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.system.domain.VideoLearningRecord;
import com.ruoyi.system.service.IVideoLearningRecordService;
import org.junit.jupiter.api.DisplayName;
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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * VideoLearningRecordController 的单元测试类
 *
 * @see com.ruoyi.web.controller.VideoLearningRecordController
 */
@WebMvcTest(VideoLearningRecordController.class)
@DisplayName("视频学习记录Controller测试")
class VideoLearningRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IVideoLearningRecordService videoLearningRecordService;

    //--- 模拟框架依赖 ---
    @MockBean
    private RedisCache redisCache;
    @MockBean
    private TokenService tokenService;

    private static final String ADMIN_USER = "admin";
    private static final String STUDENT_USER = "student";
    private static final long STUDENT_USER_ID = 100L;
    private static final String PERMISSION_PREFIX = "system:videoLearningRecord:";

    private VideoLearningRecord createTestRecord(Long recordId, Long userId) {
        VideoLearningRecord record = new VideoLearningRecord();
        record.setRecordId(recordId);
        record.setUserId(userId);
        //  record.setVideoId(1L);
        return record;
    }

    private void setupAdminUser(MockedStatic<SecurityUtils> mockedSecurityUtils) {
        mockedSecurityUtils.when(SecurityUtils::isStudent).thenReturn(false);
    }

    private void setupStudentUser(MockedStatic<SecurityUtils> mockedSecurityUtils, Long userId) {
        mockedSecurityUtils.when(SecurityUtils::isStudent).thenReturn(true);
        mockedSecurityUtils.when(SecurityUtils::getUserId).thenReturn(userId);
    }

    // --- Admin Role Tests ---

    @Test
    @WithMockUser(username = ADMIN_USER, authorities = PERMISSION_PREFIX + "list")
    @DisplayName("[管理员] 测试查询学习记录列表 - 成功")
    void testList_Admin_Success() throws Exception {
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
            setupAdminUser(mockedSecurityUtils);
            when(videoLearningRecordService.selectVideoLearningRecordList(any(VideoLearningRecord.class)))
                    .thenReturn(Collections.singletonList(new VideoLearningRecord()));

            mockMvc.perform(get("/system/videoLearningRecord/list"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.total").value(1));
            verify(videoLearningRecordService).selectVideoLearningRecordList(any(VideoLearningRecord.class));
        }
    }

    @Test
    @WithMockUser(username = ADMIN_USER, authorities = PERMISSION_PREFIX + "export")
    @DisplayName("[管理员] 测试导出学习记录 - 成功")
    void testExport_Admin_Success() throws Exception {
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
            setupAdminUser(mockedSecurityUtils);
            when(videoLearningRecordService.selectVideoLearningRecordList(any(VideoLearningRecord.class))).thenReturn(Collections.emptyList());

            mockMvc.perform(post("/system/videoLearningRecord/export").with(csrf()))
                    .andExpect(status().isOk());

            verify(videoLearningRecordService).selectVideoLearningRecordList(any(VideoLearningRecord.class));
        }
    }

    @Test
    @WithMockUser(username = ADMIN_USER, authorities = PERMISSION_PREFIX + "query")
    @DisplayName("[管理员] 测试根据ID获取学习记录 - 成功")
    void testGetInfo_Admin_Success() throws Exception {
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
            setupAdminUser(mockedSecurityUtils);
            VideoLearningRecord record = createTestRecord(1L, 200L); // Admin can get any user's record
            when(videoLearningRecordService.selectVideoLearningRecordByRecordId(1L)).thenReturn(record);

            mockMvc.perform(get("/system/videoLearningRecord/{recordId}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.recordId").value(1L));
            verify(videoLearningRecordService).selectVideoLearningRecordByRecordId(1L);
        }
    }

    @Test
    @WithMockUser(username = ADMIN_USER, authorities = PERMISSION_PREFIX + "query")
    @DisplayName("[管理员] 测试获取学习记录详情 - 记录未找到")
    void testGetInfo_NotFound() throws Exception {
        try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
            setupAdminUser(securityUtilsMock);
            when(videoLearningRecordService.selectVideoLearningRecordByRecordId(99L)).thenReturn(null);

            mockMvc.perform(get("/system/videoLearningRecord/{recordId}", 99L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").doesNotExist());

            verify(videoLearningRecordService).selectVideoLearningRecordByRecordId(99L);
        }
    }

    @Test
    @WithMockUser(username = ADMIN_USER, authorities = PERMISSION_PREFIX + "add")
    @DisplayName("[管理员] 测试新增学习记录 - 成功")
    void testAdd_Admin_Success() throws Exception {
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
            setupAdminUser(mockedSecurityUtils);
            VideoLearningRecord record = createTestRecord(null, 200L);

            when(videoLearningRecordService.saveOrUpdate(any(VideoLearningRecord.class))).thenReturn(1);

            mockMvc.perform(post("/system/videoLearningRecord").with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(record)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));

            verify(videoLearningRecordService).saveOrUpdate(any(VideoLearningRecord.class));
        }
    }

    @Test
    @WithMockUser(username = ADMIN_USER, authorities = PERMISSION_PREFIX + "edit")
    @DisplayName("[管理员] 测试修改学习记录 - 成功")
    void testEdit_Admin_Success() throws Exception {
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
            setupAdminUser(mockedSecurityUtils);
            VideoLearningRecord record = createTestRecord(1L, 200L);
            when(videoLearningRecordService.updateVideoLearningRecord(any(VideoLearningRecord.class))).thenReturn(1);

            mockMvc.perform(put("/system/videoLearningRecord").with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(record)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));
            verify(videoLearningRecordService).updateVideoLearningRecord(any(VideoLearningRecord.class));
        }
    }

    @Test
    @WithMockUser(username = ADMIN_USER, authorities = PERMISSION_PREFIX + "remove")
    @DisplayName("[管理员] 测试删除学习记录 - 成功")
    void testRemove_Admin_Success() throws Exception {
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
            setupAdminUser(mockedSecurityUtils);
            Long[] ids = {1L, 2L};
            when(videoLearningRecordService.deleteVideoLearningRecordByRecordIds(ids)).thenReturn(2);

            mockMvc.perform(delete("/system/videoLearningRecord/{recordIds}", "1,2").with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));
            verify(videoLearningRecordService).deleteVideoLearningRecordByRecordIds(ids);
        }
    }

    // --- Student Role Tests ---

    @Test
    @WithMockUser(username = STUDENT_USER, authorities = PERMISSION_PREFIX + "list")
    @DisplayName("[学生] 测试查询学习记录列表 - 成功, 只能查自己")
    void testList_Student_Success() throws Exception {
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
            setupStudentUser(mockedSecurityUtils, STUDENT_USER_ID);

            ArgumentCaptor<VideoLearningRecord> captor = ArgumentCaptor.forClass(VideoLearningRecord.class);
            when(videoLearningRecordService.selectVideoLearningRecordList(captor.capture()))
                    .thenReturn(Collections.emptyList());

            mockMvc.perform(get("/system/videoLearningRecord/list")
                            .param("studentName", "OtherStudent") // This should be cleared
                            .param("resourceName", "Test Video")) // This should be kept
                    .andExpect(status().isOk());

            VideoLearningRecord capturedRecord = captor.getValue();
            assertEquals(STUDENT_USER_ID, capturedRecord.getUserId());
            assertNull(capturedRecord.getStudentName()); // Verify cleared
            assertEquals("Test Video", capturedRecord.getResourceName());
        }
    }

    @Test
    @WithMockUser(username = STUDENT_USER, authorities = PERMISSION_PREFIX + "export")
    @DisplayName("[学生] 测试导出学习记录 - 成功")
    void testExport_Student_Success() throws Exception {
        try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
            setupStudentUser(securityUtilsMock, STUDENT_USER_ID);
            ArgumentCaptor<VideoLearningRecord> captor = ArgumentCaptor.forClass(VideoLearningRecord.class);
            when(videoLearningRecordService.selectVideoLearningRecordList(captor.capture())).thenReturn(Collections.emptyList());

            mockMvc.perform(post("/system/videoLearningRecord/export").with(csrf()))
                    .andExpect(status().isOk());

            verify(videoLearningRecordService).selectVideoLearningRecordList(any(VideoLearningRecord.class));
            assertEquals(STUDENT_USER_ID, captor.getValue().getUserId());
        }
    }

    @Test
    @WithMockUser(username = STUDENT_USER, authorities = PERMISSION_PREFIX + "query")
    @DisplayName("[学生] 测试获取自己的学习记录 - 成功")
    void testGetInfo_Student_OwnRecord_Success() throws Exception {
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
            setupStudentUser(mockedSecurityUtils, STUDENT_USER_ID);
            VideoLearningRecord record = createTestRecord(1L, STUDENT_USER_ID);
            when(videoLearningRecordService.selectVideoLearningRecordByRecordId(1L)).thenReturn(record);

            mockMvc.perform(get("/system/videoLearningRecord/{recordId}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.recordId").value(1L));
            verify(videoLearningRecordService).selectVideoLearningRecordByRecordId(1L);
        }
    }

    @Test
    @WithMockUser(username = STUDENT_USER, authorities = PERMISSION_PREFIX + "query")
    @DisplayName("[学生] 测试获取他人的学习记录 - 失败 (无权限)")
    void testGetInfo_Student_OtherRecord_Forbidden() throws Exception {
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
            setupStudentUser(mockedSecurityUtils, STUDENT_USER_ID);
            VideoLearningRecord otherStudentRecord = createTestRecord(2L, STUDENT_USER_ID + 1);
            when(videoLearningRecordService.selectVideoLearningRecordByRecordId(2L)).thenReturn(otherStudentRecord);

            mockMvc.perform(get("/system/videoLearningRecord/{recordId}", 2L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(HttpStatus.ERROR))
                    .andExpect(jsonPath("$.msg").value("没有权限查看其他学生的学习记录"));
        }
    }

    @Test
    @WithMockUser(username = STUDENT_USER, authorities = PERMISSION_PREFIX + "add")
    @DisplayName("[学生] 测试新增学习记录 - 成功，自动设置用户ID")
    void testAdd_Student_Success() throws Exception {
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
            setupStudentUser(mockedSecurityUtils, STUDENT_USER_ID);
            VideoLearningRecord record = new VideoLearningRecord();
            record.setUserId(999L); // This should be overridden

            ArgumentCaptor<VideoLearningRecord> captor = ArgumentCaptor.forClass(VideoLearningRecord.class);
            when(videoLearningRecordService.saveOrUpdate(captor.capture())).thenReturn(1);

            mockMvc.perform(post("/system/videoLearningRecord").with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(record)))
                    .andExpect(status().isOk());

            assertEquals(STUDENT_USER_ID, captor.getValue().getUserId());
        }
    }

    @Test
    @WithMockUser(username = STUDENT_USER, authorities = PERMISSION_PREFIX + "edit")
    @DisplayName("[学生] 测试修改自己的学习记录 - 成功")
    void testEdit_Student_OwnRecord_Success() throws Exception {
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
            setupStudentUser(mockedSecurityUtils, STUDENT_USER_ID);
            VideoLearningRecord originalRecord = createTestRecord(1L, STUDENT_USER_ID);
            VideoLearningRecord updatedRecord = createTestRecord(1L, STUDENT_USER_ID);

            when(videoLearningRecordService.selectVideoLearningRecordByRecordId(1L)).thenReturn(originalRecord);
            when(videoLearningRecordService.updateVideoLearningRecord(any(VideoLearningRecord.class))).thenReturn(1);

            mockMvc.perform(put("/system/videoLearningRecord").with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatedRecord)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));

            verify(videoLearningRecordService).updateVideoLearningRecord(any(VideoLearningRecord.class));
        }
    }

    @Test
    @WithMockUser(username = STUDENT_USER, authorities = PERMISSION_PREFIX + "edit")
    @DisplayName("[学生] 测试修改不存在的记录 - 失败")
    void testEdit_Student_OwnRecord_NotFound() throws Exception {
        try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
            setupStudentUser(securityUtilsMock, STUDENT_USER_ID);
            VideoLearningRecord recordToUpdate = createTestRecord(99L, STUDENT_USER_ID);

            when(videoLearningRecordService.selectVideoLearningRecordByRecordId(99L)).thenReturn(null);
            when(videoLearningRecordService.updateVideoLearningRecord(any(VideoLearningRecord.class))).thenReturn(0);

            mockMvc.perform(put("/system/videoLearningRecord").with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(recordToUpdate)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(HttpStatus.ERROR))
                    .andExpect(jsonPath("$.msg").value("操作失败"));

            verify(videoLearningRecordService).selectVideoLearningRecordByRecordId(99L);
        }
    }

    @Test
    @WithMockUser(username = STUDENT_USER, authorities = PERMISSION_PREFIX + "edit")
    @DisplayName("[学生] 测试修改他人的学习记录 - 失败 (无权限)")
    void testEdit_Student_OtherRecord_Forbidden() throws Exception {
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
            setupStudentUser(mockedSecurityUtils, STUDENT_USER_ID);
            VideoLearningRecord otherStudentRecord = createTestRecord(2L, STUDENT_USER_ID + 1);
            when(videoLearningRecordService.selectVideoLearningRecordByRecordId(2L)).thenReturn(otherStudentRecord);

            mockMvc.perform(put("/system/videoLearningRecord").with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(otherStudentRecord)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(HttpStatus.ERROR))
                    .andExpect(jsonPath("$.msg").value("没有权限修改其他学生的学习记录"));

            verify(videoLearningRecordService, never()).updateVideoLearningRecord(any());
        }
    }

    @Test
    @WithMockUser(username = STUDENT_USER, authorities = PERMISSION_PREFIX + "remove")
    @DisplayName("[学生] 测试删除自己的学习记录 - 成功")
    void testRemove_Student_OwnRecord_Success() throws Exception {
        try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
            setupStudentUser(securityUtilsMock, STUDENT_USER_ID);
            when(videoLearningRecordService.selectVideoLearningRecordByRecordId(1L)).thenReturn(createTestRecord(1L, STUDENT_USER_ID));
            when(videoLearningRecordService.selectVideoLearningRecordByRecordId(2L)).thenReturn(createTestRecord(2L, STUDENT_USER_ID));
            when(videoLearningRecordService.deleteVideoLearningRecordByRecordIds(any(Long[].class))).thenReturn(2);

            mockMvc.perform(delete("/system/videoLearningRecord/{recordIds}", "1,2").with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));

            verify(videoLearningRecordService).deleteVideoLearningRecordByRecordIds(new Long[]{1L, 2L});
        }
    }

    @Test
    @WithMockUser(username = STUDENT_USER, authorities = PERMISSION_PREFIX + "remove")
    @DisplayName("[学生] 测试删除他人的学习记录 - 失败 (无权限)")
    void testRemove_Student_OtherRecord_Forbidden() throws Exception {
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
            setupStudentUser(mockedSecurityUtils, STUDENT_USER_ID);
            when(videoLearningRecordService.selectVideoLearningRecordByRecordId(1L)).thenReturn(createTestRecord(1L, STUDENT_USER_ID));
            when(videoLearningRecordService.selectVideoLearningRecordByRecordId(2L)).thenReturn(createTestRecord(2L, STUDENT_USER_ID + 1));

            mockMvc.perform(delete("/system/videoLearningRecord/{recordIds}", "1,2").with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(HttpStatus.ERROR))
                    .andExpect(jsonPath("$.msg").value("没有权限删除其他学生的学习记录"));

            verify(videoLearningRecordService, never()).deleteVideoLearningRecordByRecordIds(any());
        }
    }
}