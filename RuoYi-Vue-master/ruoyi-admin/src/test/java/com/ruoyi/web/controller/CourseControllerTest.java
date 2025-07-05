package com.ruoyi.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.framework.config.ServerConfig;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.system.domain.Course;
import com.ruoyi.system.service.IAIGradingService;
import com.ruoyi.system.service.ICourseService;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseController.class)
@DisplayName("课程信息Controller测试")
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ICourseService courseService;
    @MockBean
    private ServerConfig serverConfig;
    @MockBean
    private IAIGradingService aiGradingService;
    @MockBean
    private RedisCache redisCache;
    @MockBean
    private TokenService tokenService;

    private static final String MOCK_USER = "testUser";

    @Test
    @WithMockUser(username = MOCK_USER, authorities = {"system:course:list"})
    @DisplayName("测试查询课程列表 - 成功")
    void testList_Success() throws Exception {
        when(courseService.selectCourseList(any(Course.class))).thenReturn(Collections.singletonList(new Course()));
        mockMvc.perform(get("/system/course/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1));
        verify(courseService).selectCourseList(any(Course.class));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = {"system:course:export"})
    @DisplayName("测试导出课程列表 - 成功")
    void testExport_Success() throws Exception {
        when(courseService.selectCourseList(any(Course.class))).thenReturn(Collections.emptyList());
        mockMvc.perform(post("/system/course/export").with(csrf()))
                .andExpect(status().isOk());
        verify(courseService).selectCourseList(any(Course.class));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = {"system:course:query"})
    @DisplayName("测试根据ID获取课程信息 - 成功")
    void testGetInfo_Success() throws Exception {
        long courseId = 1L;
        Course course = new Course();
        course.setCourseId(courseId);
        course.setCourseName("测试课程");
        when(courseService.selectCourseByCourseId(courseId)).thenReturn(course);

        mockMvc.perform(get("/system/course/{courseId}", courseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS))
                .andExpect(jsonPath("$.data.courseName").value("测试课程"));
        verify(courseService).selectCourseByCourseId(courseId);
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = {"system:course:query"})
    @DisplayName("测试根据ID获取课程信息 - 未找到")
    void testGetInfo_NotFound() throws Exception {
        when(courseService.selectCourseByCourseId(anyLong())).thenReturn(null);
        mockMvc.perform(get("/system/course/{courseId}", 99L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = {"system:course:add"})
    @DisplayName("测试新增课程 - 成功")
    void testAdd_Success() throws Exception {
        Course course = new Course();
        course.setCourseName("新课程");
        when(courseService.insertCourse(any(Course.class))).thenReturn(1);

        mockMvc.perform(post("/system/course").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(course)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = {"system:course:add"})
    @DisplayName("测试新增课程 - 失败")
    void testAdd_Failure() throws Exception {
        Course course = new Course();
        course.setCourseName("新课程");
        when(courseService.insertCourse(any(Course.class))).thenReturn(0);

        mockMvc.perform(post("/system/course").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(course)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.ERROR));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = {"system:course:edit"})
    @DisplayName("测试修改课程 - 成功")
    void testEdit_Success() throws Exception {
        Course course = new Course();
        course.setCourseId(1L);
        when(courseService.updateCourse(any(Course.class))).thenReturn(1);

        mockMvc.perform(put("/system/course").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(course)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = {"system:course:edit"})
    @DisplayName("测试修改课程 - 失败")
    void testEdit_Failure() throws Exception {
        Course course = new Course();
        course.setCourseId(1L);
        when(courseService.updateCourse(any(Course.class))).thenReturn(0);

        mockMvc.perform(put("/system/course").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(course)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.ERROR));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = {"system:course:remove"})
    @DisplayName("测试删除课程 - 成功")
    void testRemove_Success() throws Exception {
        Long[] courseIds = {1L, 2L};
        when(courseService.deleteCourseByCourseIds(courseIds)).thenReturn(2);

        mockMvc.perform(delete("/system/course/{courseIds}", "1,2").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = {"system:course:remove"})
    @DisplayName("测试删除课程 - 失败")
    void testRemove_Failure() throws Exception {
        Long[] courseIds = {99L};
        when(courseService.deleteCourseByCourseIds(courseIds)).thenReturn(0);

        mockMvc.perform(delete("/system/course/{courseIds}", "99").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.ERROR));
    }

    @Test
    @WithMockUser(username = MOCK_USER)
    @DisplayName("测试课程图片上传 - 成功")
    void testUploadFile_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "test image".getBytes());
        String newFileName = "generated_uuid_test.jpg";
        String serverUrl = "http://localhost:8080";

        try (MockedStatic<RuoYiConfig> ruoyiConfigMock = Mockito.mockStatic(RuoYiConfig.class);
             MockedStatic<FileUploadUtils> fileUploadUtilsMock = Mockito.mockStatic(FileUploadUtils.class)) {

            ruoyiConfigMock.when(RuoYiConfig::getUploadPath).thenReturn("/mock/upload/path");
            fileUploadUtilsMock.when(() -> FileUploadUtils.upload(anyString(), any(MultipartFile.class))).thenReturn(newFileName);
            when(serverConfig.getUrl()).thenReturn(serverUrl);

            mockMvc.perform(multipart("/system/course/upload").file(file).with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS))
                    .andExpect(jsonPath("$.fileName").value(newFileName))
                    .andExpect(jsonPath("$.url").value(serverUrl + newFileName));
        }
    }

    @Test
    @WithMockUser(username = MOCK_USER)
    @DisplayName("测试课程图片上传 - 失败")
    void testUploadFile_Failure() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "test image".getBytes());

        try (MockedStatic<RuoYiConfig> ruoyiConfigMock = Mockito.mockStatic(RuoYiConfig.class);
             MockedStatic<FileUploadUtils> fileUploadUtilsMock = Mockito.mockStatic(FileUploadUtils.class)) {

            ruoyiConfigMock.when(RuoYiConfig::getUploadPath).thenReturn("/mock/upload/path");
            fileUploadUtilsMock.when(() -> FileUploadUtils.upload(anyString(), any(MultipartFile.class)))
                    .thenThrow(new IOException("磁盘空间不足"));

            mockMvc.perform(multipart("/system/course/upload").file(file).with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(HttpStatus.ERROR))
                    .andExpect(jsonPath("$.msg").value("磁盘空间不足"));
        }
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = {"system:course:grade"})
    @DisplayName("测试AI评分接口 - 成功")
    void testAiGrade_Success() throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("courseId", 1L);
        requestBody.put("homeworkId", 2L);
        requestBody.put("submissionId", 3L);

        // 使用您提供的 AIGradingResult.ok() 工厂方法
        IAIGradingService.AIGradingResult mockResult = IAIGradingService.AIGradingResult.ok(95, "干得漂亮！");

        when(aiGradingService.gradeHomework(anyLong(), anyLong(), anyLong())).thenReturn(mockResult);

        mockMvc.perform(post("/system/course/ai-grade").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS))
                .andExpect(jsonPath("$.score").value(95)) // <-- 已修正为整数
                .andExpect(jsonPath("$.comment").value("干得漂亮！"));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = {"system:course:grade"})
    @DisplayName("测试AI评分接口 - AI服务返回失败")
    void testAiGrade_ServiceFailure() throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("courseId", 1L);
        requestBody.put("homeworkId", 2L);
        requestBody.put("submissionId", 3L);

        // 使用您提供的 AIGradingResult.fail() 工厂方法
        IAIGradingService.AIGradingResult mockResult = IAIGradingService.AIGradingResult.fail("AI模型连接超时");

        when(aiGradingService.gradeHomework(anyLong(), anyLong(), anyLong())).thenReturn(mockResult);

        mockMvc.perform(post("/system/course/ai-grade").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.ERROR))
                .andExpect(jsonPath("$.msg").value("AI模型连接超时"));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = {"system:course:grade"})
    @DisplayName("测试AI评分接口 - 缺少参数courseId")
    void testAiGrade_MissingCourseId() throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("homeworkId", 2L);
        requestBody.put("submissionId", 3L);

        mockMvc.perform(post("/system/course/ai-grade").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.ERROR))
                .andExpect(jsonPath("$.msg").value("参数courseId缺失"));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = {"system:course:grade"})
    @DisplayName("测试AI评分接口 - 缺少参数homeworkId")
    void testAiGrade_MissingHomeworkId() throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("courseId", 1L);
        requestBody.put("submissionId", 3L);

        mockMvc.perform(post("/system/course/ai-grade").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.ERROR))
                .andExpect(jsonPath("$.msg").value("参数homeworkId缺失"));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = {"system:course:grade"})
    @DisplayName("测试AI评分接口 - 通用异常")
    void testAiGrade_GenericException() throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("courseId", 1L);
        requestBody.put("homeworkId", 2L);
        requestBody.put("submissionId", 3L);

        when(aiGradingService.gradeHomework(anyLong(), anyLong(), anyLong())).thenThrow(new RuntimeException("模拟未知错误"));

        mockMvc.perform(post("/system/course/ai-grade").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.ERROR))
                .andExpect(jsonPath("$.msg").value("AI评分失败: 模拟未知错误"));
    }
}