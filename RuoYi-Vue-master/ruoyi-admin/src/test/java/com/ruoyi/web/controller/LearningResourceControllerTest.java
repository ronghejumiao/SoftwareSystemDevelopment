package com.ruoyi.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.framework.config.ServerConfig;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.system.domain.LearningResource;
import com.ruoyi.system.service.ILearningResourceService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * LearningResourceController 的单元测试类
 *
 * @see com.ruoyi.web.controller.LearningResourceController
 */
@WebMvcTest(LearningResourceController.class)
@DisplayName("学习资源Controller测试")
class LearningResourceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ILearningResourceService learningResourceService;
    @MockBean
    private ServerConfig serverConfig;

    //--- 模拟框架依赖 ---
    @MockBean
    private RedisCache redisCache;
    @MockBean
    private TokenService tokenService;

    private static final String MOCK_USER = "testUser";
    private static final String PERMISSION_PREFIX = "system:resource:";

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "list")
    @DisplayName("测试查询学习资源列表 - 成功")
    void testList_Success() throws Exception {
        when(learningResourceService.selectLearningResourceList(any(LearningResource.class)))
                .thenReturn(Collections.singletonList(new LearningResource()));

        mockMvc.perform(get("/system/resource/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "export")
    @DisplayName("测试导出学习资源列表 - 成功")
    void testExport_Success() throws Exception {
        when(learningResourceService.selectLearningResourceList(any(LearningResource.class)))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(post("/system/resource/export").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8"));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "query")
    @DisplayName("测试根据ID获取学习资源 - 成功")
    void testGetInfo_Success() throws Exception {
        long resourceId = 1L;
        LearningResource resource = new LearningResource();
        resource.setResourceId(resourceId);
        when(learningResourceService.selectLearningResourceByResourceId(resourceId)).thenReturn(resource);

        mockMvc.perform(get("/system/resource/{resourceId}", resourceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.resourceId").value(resourceId));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "query")
    @DisplayName("测试根据ID获取学习资源 - 未找到")
    void testGetInfo_NotFound() throws Exception {
        when(learningResourceService.selectLearningResourceByResourceId(anyLong())).thenReturn(null);
        mockMvc.perform(get("/system/resource/{resourceId}", 99L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "add")
    @DisplayName("测试新增学习资源 - 成功")
    void testAdd_Success() throws Exception {
        LearningResource resource = new LearningResource();
        when(learningResourceService.insertLearningResource(any(LearningResource.class))).thenReturn(1);

        mockMvc.perform(post("/system/resource").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resource)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "add")
    @DisplayName("测试新增学习资源 - 失败")
    void testAdd_Failure() throws Exception {
        when(learningResourceService.insertLearningResource(any(LearningResource.class))).thenReturn(0);
        mockMvc.perform(post("/system/resource").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LearningResource())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.ERROR));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "edit")
    @DisplayName("测试修改学习资源 - 成功")
    void testEdit_Success() throws Exception {
        LearningResource resource = new LearningResource();
        when(learningResourceService.updateLearningResource(any(LearningResource.class))).thenReturn(1);
        mockMvc.perform(put("/system/resource").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resource)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "edit")
    @DisplayName("测试修改学习资源 - 失败")
    void testEdit_Failure() throws Exception {
        when(learningResourceService.updateLearningResource(any(LearningResource.class))).thenReturn(0);
        mockMvc.perform(put("/system/resource").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LearningResource())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.ERROR));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "remove")
    @DisplayName("测试删除学习资源 - 成功")
    void testRemove_Success() throws Exception {
        Long[] ids = {1L, 2L};
        when(learningResourceService.deleteLearningResourceByResourceIds(ids)).thenReturn(2);
        mockMvc.perform(delete("/system/resource/{resourceIds}", "1,2").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "remove")
    @DisplayName("测试删除学习资源 - 失败")
    void testRemove_Failure() throws Exception {
        Long[] ids = {99L};
        when(learningResourceService.deleteLearningResourceByResourceIds(ids)).thenReturn(0);
        mockMvc.perform(delete("/system/resource/{resourceIds}", "99").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.ERROR));
    }

    @Test
    @WithMockUser(username = MOCK_USER)
    @DisplayName("测试学习资源文件上传 - 成功")
    void testUploadFile_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "resource.pdf", MediaType.APPLICATION_PDF_VALUE, "some content".getBytes());
        String newFileName = "/profile/upload/resource.pdf";
        String serverUrl = "http://localhost:8080";

        try (MockedStatic<RuoYiConfig> ruoyiConfigMock = Mockito.mockStatic(RuoYiConfig.class);
             MockedStatic<FileUploadUtils> fileUploadUtilsMock = Mockito.mockStatic(FileUploadUtils.class)) {

            ruoyiConfigMock.when(RuoYiConfig::getProfile).thenReturn("/profile/upload");
            fileUploadUtilsMock.when(() -> FileUploadUtils.upload(anyString(), any(MultipartFile.class))).thenReturn(newFileName);
            when(serverConfig.getUrl()).thenReturn(serverUrl);

            mockMvc.perform(multipart("/system/resource/upload").file(file).with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS))
                    .andExpect(jsonPath("$.fileName").value(newFileName))
                    .andExpect(jsonPath("$.url").value(serverUrl + newFileName));
        }
    }

    @Test
    @WithMockUser(username = MOCK_USER)
    @DisplayName("测试学习资源文件上传 - 失败(IO异常)")
    void testUploadFile_IOException() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "resource.pdf", MediaType.APPLICATION_PDF_VALUE, "some content".getBytes());

        try (MockedStatic<RuoYiConfig> ruoyiConfigMock = Mockito.mockStatic(RuoYiConfig.class);
             MockedStatic<FileUploadUtils> fileUploadUtilsMock = Mockito.mockStatic(FileUploadUtils.class)) {

            ruoyiConfigMock.when(RuoYiConfig::getProfile).thenReturn("/profile/upload");
            fileUploadUtilsMock.when(() -> FileUploadUtils.upload(anyString(), any(MultipartFile.class)))
                    .thenThrow(new IOException("模拟上传失败"));

            mockMvc.perform(multipart("/system/resource/upload").file(file).with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(HttpStatus.ERROR))
                    .andExpect(jsonPath("$.msg").value("模拟上传失败"));
        }
    }
}