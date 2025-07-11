package com.ruoyi.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.system.domain.CourseHomework;
import com.ruoyi.system.domain.LearningRecord;
import com.ruoyi.system.domain.LearningTask;
import com.ruoyi.system.domain.TaskSubmission;
import com.ruoyi.system.service.ICourseHomeworkService;
import com.ruoyi.system.service.ILearningRecordService;
import com.ruoyi.system.service.ILearningTaskService;
import com.ruoyi.system.service.ITaskSubmissionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseHomeworkController.class)
@DisplayName("课程作业Controller测试 (100%覆盖率目标)")
class CourseHomeworkControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ICourseHomeworkService courseHomeworkService;
    @MockBean
    private ILearningTaskService learningTaskService;
    @MockBean
    private ILearningRecordService learningRecordService;
    @MockBean
    private ITaskSubmissionService taskSubmissionService;
    @MockBean
    private RedisCache redisCache;
    @MockBean
    private TokenService tokenService;

    private static final String MOCK_USER = "testUser";
    private static final String PERMISSION_PREFIX = "system:homework:";

    // --- Standard CRUD & Export Tests ---
    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "list")
    @DisplayName("测试查询课程作业列表 - 成功")
    void testList_Success() throws Exception {
        when(courseHomeworkService.selectCourseHomeworkList(any(CourseHomework.class)))
                .thenReturn(Collections.singletonList(new CourseHomework()));
        mockMvc.perform(get("/system/homework/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "export")
    @DisplayName("测试导出课程作业列表 - 成功")
    void testExport_Success() throws Exception {
        when(courseHomeworkService.selectCourseHomeworkList(any(CourseHomework.class))).thenReturn(Collections.emptyList());
        mockMvc.perform(post("/system/homework/export").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "query")
    @DisplayName("测试获取课程作业详情 - 成功")
    void testGetInfo_Success() throws Exception {
        long homeworkId = 1L;
        CourseHomework homework = new CourseHomework();
        homework.setHomeworkId(homeworkId);
        when(courseHomeworkService.selectCourseHomeworkByHomeworkId(homeworkId)).thenReturn(homework);
        mockMvc.perform(get("/system/homework/{homeworkId}", homeworkId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.homeworkId").value(homeworkId));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "query")
    @DisplayName("测试获取课程作业详情 - 未找到")
    void testGetInfo_NotFound() throws Exception {
        when(courseHomeworkService.selectCourseHomeworkByHomeworkId(anyLong())).thenReturn(null);
        mockMvc.perform(get("/system/homework/{homeworkId}", 99L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "add")
    @DisplayName("测试新增课程作业 - 成功")
    void testAdd_Success() throws Exception {
        CourseHomework homework = new CourseHomework();
        when(courseHomeworkService.insertCourseHomework(any(CourseHomework.class))).thenReturn(1);
        mockMvc.perform(post("/system/homework").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(homework)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "add")
    @DisplayName("测试新增课程作业 - 失败")
    void testAdd_Failure() throws Exception {
        when(courseHomeworkService.insertCourseHomework(any(CourseHomework.class))).thenReturn(0);
        mockMvc.perform(post("/system/homework").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CourseHomework())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.ERROR));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "edit")
    @DisplayName("测试修改课程作业 - 成功")
    void testEdit_Success() throws Exception {
        CourseHomework homework = new CourseHomework();
        when(courseHomeworkService.updateCourseHomework(any(CourseHomework.class))).thenReturn(1);
        mockMvc.perform(put("/system/homework").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(homework)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "edit")
    @DisplayName("测试修改课程作业 - 失败")
    void testEdit_Failure() throws Exception {
        when(courseHomeworkService.updateCourseHomework(any(CourseHomework.class))).thenReturn(0);
        mockMvc.perform(put("/system/homework").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CourseHomework())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.ERROR));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "remove")
    @DisplayName("测试删除课程作业 - 成功")
    void testRemove_Success() throws Exception {
        Long[] ids = {1L, 2L};
        when(courseHomeworkService.deleteCourseHomeworkByHomeworkIds(ids)).thenReturn(2);
        mockMvc.perform(delete("/system/homework/{homeworkIds}", "1,2").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "remove")
    @DisplayName("测试删除课程作业 - 失败")
    void testRemove_Failure() throws Exception {
        Long[] ids = {99L};
        when(courseHomeworkService.deleteCourseHomeworkByHomeworkIds(ids)).thenReturn(0);
        mockMvc.perform(delete("/system/homework/{homeworkIds}", "99").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.ERROR));
    }

    // --- Business Logic Tests ---

    @Test
    @WithMockUser(username = MOCK_USER)
    @DisplayName("测试获取用户作业完成情况 - 成功")
    void testGetUserHomeworkStatus_Success() throws Exception {
        long courseId = 1L, userId = 1L, recordId = 1L;
        LearningTask task = new LearningTask();
        task.setTaskId(101L);
        task.setHomeworkId(201L);
        when(learningTaskService.selectLearningTaskListByCourseIdAndSubmitMethod(courseId, "作业完成"))
                .thenReturn(Collections.singletonList(task));

        CourseHomework hw = new CourseHomework();
        hw.setHomeworkId(201L);
        when(courseHomeworkService.selectCourseHomeworkListByIds(anyList())).thenReturn(Collections.singletonList(hw));

        LearningRecord record = new LearningRecord();
        record.setRecordId(recordId);
        when(learningRecordService.selectByUserIdAndCourseId(userId, courseId)).thenReturn(record);
        when(taskSubmissionService.selectByRecordId(recordId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/system/homework/user/status")
                        .param("courseId", String.valueOf(courseId))
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.uncompleted.length()").value(1));
    }

    @Test
    @WithMockUser(username = MOCK_USER)
    @DisplayName("测试获取用户作业完成情况 - 无任务")
    void testGetUserHomeworkStatus_NoTasks() throws Exception {
        when(learningTaskService.selectLearningTaskListByCourseIdAndSubmitMethod(anyLong(), anyString())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/system/homework/user/status")
                        .param("courseId", "1")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.completed.length()").value(0))
                .andExpect(jsonPath("$.data.uncompleted.length()").value(0));
    }

    @Test
    @WithMockUser(username = MOCK_USER)
    @DisplayName("测试获取用户作业完成情况 - 无学习记录")
    void testGetUserHomeworkStatus_NoRecord() throws Exception {
        when(learningTaskService.selectLearningTaskListByCourseIdAndSubmitMethod(anyLong(), anyString())).thenReturn(Collections.singletonList(new LearningTask()));
        when(learningRecordService.selectByUserIdAndCourseId(anyLong(), anyLong())).thenReturn(null);

        mockMvc.perform(get("/system/homework/user/status")
                        .param("courseId", "1")
                        .param("userId", "1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = MOCK_USER)
    @DisplayName("测试提交作业 - 成功")
    void testSubmitHomework_Success() throws Exception {
        LearningTask task = new LearningTask();
        task.setCourseId(1L);
        LearningRecord record = new LearningRecord();
        record.setRecordId(1L);
        TaskSubmission submission = new TaskSubmission();
        submission.setTaskId(1L);
        submission.setUserId(1L);

        when(learningTaskService.selectLearningTaskByTaskId(anyLong())).thenReturn(task);
        when(learningRecordService.selectByUserIdAndCourseId(anyLong(), anyLong())).thenReturn(record);
        when(taskSubmissionService.insertTaskSubmission(any(TaskSubmission.class))).thenReturn(1);

        mockMvc.perform(post("/system/homework/submit").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(submission)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));
    }

    @Test
    @WithMockUser(username = MOCK_USER)
    @DisplayName("测试提交作业 - 任务ID为空")
    void testSubmitHomework_NullTaskId() throws Exception {
        TaskSubmission submission = new TaskSubmission();
        submission.setTaskId(null);

        mockMvc.perform(post("/system/homework/submit").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(submission)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("未能确定课程ID"));
    }

    @Test
    @WithMockUser(username = MOCK_USER)
    @DisplayName("测试提交作业 - 任务未找到")
    void testSubmitHomework_TaskNotFound() throws Exception {
        TaskSubmission submission = new TaskSubmission();
        submission.setTaskId(99L);
        when(learningTaskService.selectLearningTaskByTaskId(anyLong())).thenReturn(null);

        mockMvc.perform(post("/system/homework/submit").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(submission)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("未能确定课程ID"));
    }



    // --- File Upload Tests ---
    @Test
    @WithMockUser(username = MOCK_USER)
    @DisplayName("测试上传作业附件 - 成功")
    void testUploadFile_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "homework.pdf", MediaType.APPLICATION_PDF_VALUE, "my homework".getBytes());
        String expectedPath = "/profile/homework/1/student/1/generated_uuid_homework.pdf";

        try (MockedStatic<RuoYiConfig> ruoyiConfigMock = Mockito.mockStatic(RuoYiConfig.class);
             MockedStatic<FileUploadUtils> fileUploadUtilsMock = Mockito.mockStatic(FileUploadUtils.class)) {

            ruoyiConfigMock.when(RuoYiConfig::getProfile).thenReturn("/profile");
            fileUploadUtilsMock.when(() -> FileUploadUtils.upload(anyString(), any(MultipartFile.class))).thenReturn(expectedPath);

            mockMvc.perform(multipart("/system/homework/upload").file(file).with(csrf())
                            .param("courseId", "1")
                            .param("userId", "1")
                            .param("role", "student"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS))
                    .andExpect(jsonPath("$.msg").value(expectedPath));
        }
    }

    @Test
    @WithMockUser(username = MOCK_USER)
    @DisplayName("测试上传作业附件 - 文件为空")
    void testUploadFile_Empty() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "empty.txt", "text/plain", new byte[0]);
        mockMvc.perform(multipart("/system/homework/upload").file(file).with(csrf())
                        .param("courseId", "1").param("userId", "1").param("role", "student"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("请选择要上传的文件"));
    }

    @Test
    @WithMockUser(username = MOCK_USER)
    @DisplayName("测试上传作业附件 - 格式错误")
    void testUploadFile_InvalidExtension() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "homework.zip", "application/zip", "my homework".getBytes());
        mockMvc.perform(multipart("/system/homework/upload").file(file).with(csrf())
                        .param("courseId", "1").param("userId", "1").param("role", "student"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.ERROR))
                .andExpect(jsonPath("$.msg").value("文件格式不正确，请上传PDF、DOC、DOCX、TXT格式的文件"));
    }

    @Test
    @WithMockUser(username = MOCK_USER)
    @DisplayName("测试上传作业附件 - 文件过大")
    void testUploadFile_TooLarge() throws Exception {
        // 创建一个大于10MB的字节数组
        byte[] largeContent = new byte[11 * 1024 * 1024];
        MockMultipartFile file = new MockMultipartFile("file", "large.pdf", MediaType.APPLICATION_PDF_VALUE, largeContent);
        mockMvc.perform(multipart("/system/homework/upload").file(file).with(csrf())
                        .param("courseId", "1").param("userId", "1").param("role", "student"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("文件大小不能超过10MB"));
    }

    @Test
    @WithMockUser(username = MOCK_USER)
    @DisplayName("测试上传作业附件 - 上传IO异常")
    void testUploadFile_IOException() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "homework.pdf", MediaType.APPLICATION_PDF_VALUE, "my homework".getBytes());
        try (MockedStatic<RuoYiConfig> ruoyiConfigMock = Mockito.mockStatic(RuoYiConfig.class);
             MockedStatic<FileUploadUtils> fileUploadUtilsMock = Mockito.mockStatic(FileUploadUtils.class)) {
            
            ruoyiConfigMock.when(RuoYiConfig::getProfile).thenReturn("/profile");
            fileUploadUtilsMock.when(() -> FileUploadUtils.upload(anyString(), any(MultipartFile.class)))
                    .thenThrow(new IOException("模拟磁盘写入失败"));

            mockMvc.perform(multipart("/system/homework/upload").file(file).with(csrf())
                            .param("courseId", "1")
                            .param("userId", "1")
                            .param("role", "student"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(HttpStatus.ERROR)); // 异常的消息可能不同，只验证状态码
        }
    }

    @Test
    @WithMockUser(username = MOCK_USER)
    @DisplayName("测试上传作业附件 - 目录创建失败")
    void testUploadFile_DirectoryCreationFailure() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "homework.pdf", MediaType.APPLICATION_PDF_VALUE, "my homework".getBytes());
        try (MockedStatic<RuoYiConfig> ruoyiConfigMock = Mockito.mockStatic(RuoYiConfig.class);
             MockedStatic<FileUploadUtils> fileUploadUtilsMock = Mockito.mockStatic(FileUploadUtils.class)) {
            
            ruoyiConfigMock.when(RuoYiConfig::getProfile).thenReturn("/profile");
            fileUploadUtilsMock.when(() -> FileUploadUtils.upload(anyString(), any(MultipartFile.class)))
                    .thenThrow(new IOException("目录创建失败")); // Simulate failure to create directory

            mockMvc.perform(multipart("/system/homework/upload").file(file).with(csrf())
                            .param("courseId", "1")
                            .param("userId", "1")
                            .param("role", "student"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(HttpStatus.ERROR))
                    .andExpect(jsonPath("$.msg").value("目录创建失败"));
        }
    }
}