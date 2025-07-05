package com.ruoyi.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.VideoUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.system.domain.VideoResource;
import com.ruoyi.system.service.IVideoResourceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * VideoResourceController 的单元测试类
 *
 * @see com.ruoyi.web.controller.VideoResourceController
 */
@WebMvcTest(VideoResourceController.class)
@DisplayName("视频资源Controller测试")
class VideoResourceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IVideoResourceService videoResourceService;

    //--- 模拟框架依赖 ---
    @MockBean
    private RedisCache redisCache;
    @MockBean
    private TokenService tokenService;

    private static final String MOCK_USER = "testUser";
    private static final String PERMISSION_PREFIX = "system:videoresource:";

    private VideoResource createTestVideoResource() {
        VideoResource videoResource = new VideoResource();
        videoResource.setVideoId(1L);
        videoResource.setCourseId(1L);
        videoResource.setVideoName("测试视频");
        videoResource.setVideoPath("/profile/video/test.mp4");
        videoResource.setUploadTime(new Date());
        return videoResource;
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "list")
    @DisplayName("测试查询视频资源列表 - 成功")
    void testList_Success() throws Exception {
        when(videoResourceService.selectVideoResourceList(any(VideoResource.class)))
                .thenReturn(Collections.singletonList(new VideoResource()));

        mockMvc.perform(get("/system/videoresource/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1));
        verify(videoResourceService).selectVideoResourceList(any(VideoResource.class));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "export")
    @DisplayName("测试导出视频资源列表 - 成功")
    void testExport_Success() throws Exception {
        when(videoResourceService.selectVideoResourceList(any(VideoResource.class)))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(post("/system/videoresource/export").with(csrf()))
                .andExpect(status().isOk());
        verify(videoResourceService).selectVideoResourceList(any(VideoResource.class));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "query")
    @DisplayName("测试根据ID获取视频资源 - 成功")
    void testGetInfo_Success() throws Exception {
        long videoId = 1L;
        VideoResource videoResource = createTestVideoResource();
        when(videoResourceService.selectVideoResourceByVideoId(videoId)).thenReturn(videoResource);

        mockMvc.perform(get("/system/videoresource/{videoId}", videoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS))
                .andExpect(jsonPath("$.data.videoId").value(videoId));
        verify(videoResourceService).selectVideoResourceByVideoId(videoId);
    }



    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "add")
    @DisplayName("测试上传视频文件 - 成功")
    void testUploadVideo_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.mp4", "video/mp4", "content".getBytes());
        String videoPath = "/profile/video/uuid_test.mp4";
        String coverImage = "/profile/video/uuid_test.jpg";

        try (MockedStatic<RuoYiConfig> ruoyiConfigMock = Mockito.mockStatic(RuoYiConfig.class);
             MockedStatic<FileUploadUtils> fileUploadUtilsMock = Mockito.mockStatic(FileUploadUtils.class);
             MockedStatic<VideoUtils> videoUtilsMock = Mockito.mockStatic(VideoUtils.class)) {

            ruoyiConfigMock.when(RuoYiConfig::getProfile).thenReturn("/profile");
            fileUploadUtilsMock.when(() -> FileUploadUtils.upload(anyString(), any(MultipartFile.class))).thenReturn(videoPath);
            videoUtilsMock.when(() -> VideoUtils.generateFirstFrame(anyString())).thenReturn(coverImage);

            mockMvc.perform(multipart("/system/videoresource/uploadVideo").file(file).with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS))
                    .andExpect(jsonPath("$.videoUrl").value(videoPath))
                    .andExpect(jsonPath("$.coverImage").value(coverImage));
        }
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "add")
    @DisplayName("测试上传视频文件 - 空文件")
    void testUploadVideo_EmptyFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.mp4", "video/mp4", new byte[0]);

        mockMvc.perform(multipart("/system/videoresource/uploadVideo").file(file).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.ERROR))
                .andExpect(jsonPath("$.msg").value("请选择要上传的视频文件"));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "add")
    @DisplayName("测试上传视频文件 - 格式错误")
    void testUploadVideo_InvalidFormat() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.avi", "video/x-msvideo", "content".getBytes());

        mockMvc.perform(multipart("/system/videoresource/uploadVideo").file(file).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.ERROR))
                .andExpect(jsonPath("$.msg").value("不支持的视频格式，仅支持mp4格式"));
    }
    
    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "add")
    @DisplayName("测试上传视频文件 - 上传失败")
    void testUploadVideo_UploadFailure() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.mp4", "video/mp4", "content".getBytes());

        try (MockedStatic<RuoYiConfig> ruoyiConfigMock = Mockito.mockStatic(RuoYiConfig.class);
             MockedStatic<FileUploadUtils> fileUploadUtilsMock = Mockito.mockStatic(FileUploadUtils.class)) {
            
            ruoyiConfigMock.when(RuoYiConfig::getProfile).thenReturn("/profile");
            fileUploadUtilsMock.when(() -> FileUploadUtils.upload(anyString(), any(MultipartFile.class)))
                    .thenThrow(new IOException("模拟上传失败"));

            mockMvc.perform(multipart("/system/videoresource/uploadVideo").file(file).with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(HttpStatus.ERROR))
                    .andExpect(jsonPath("$.msg").value("视频处理失败: 模拟上传失败"));
        }
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "add")
    @DisplayName("测试上传封面图片 - 成功")
    void testUploadThumbnail_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "thumbnail.jpg", MediaType.IMAGE_JPEG_VALUE, "content".getBytes());
        String thumbnailUrl = "/profile/upload/uuid_thumbnail.jpg";

        try (MockedStatic<RuoYiConfig> ruoyiConfigMock = Mockito.mockStatic(RuoYiConfig.class);
             MockedStatic<FileUploadUtils> fileUploadUtilsMock = Mockito.mockStatic(FileUploadUtils.class)) {
            
            ruoyiConfigMock.when(RuoYiConfig::getUploadPath).thenReturn("/profile/upload");
            fileUploadUtilsMock.when(() -> FileUploadUtils.upload(anyString(), any(MultipartFile.class), any())).thenReturn(thumbnailUrl);

            mockMvc.perform(multipart("/system/videoresource/uploadThumbnail").file(file).with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS))
                    .andExpect(jsonPath("$.thumbnailUrl").value(thumbnailUrl));
        }
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "add")
    @DisplayName("测试上传封面图片 - 空文件")
    void testUploadThumbnail_EmptyFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "thumbnail.jpg", MediaType.IMAGE_JPEG_VALUE, new byte[0]);

        mockMvc.perform(multipart("/system/videoresource/uploadThumbnail").file(file).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.ERROR))
                .andExpect(jsonPath("$.msg").value("请选择要上传的封面图片"));
    }
    
    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "add")
    @DisplayName("测试上传封面图片 - 上传失败")
    void testUploadThumbnail_UploadFailure() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "thumbnail.jpg", MediaType.IMAGE_JPEG_VALUE, "content".getBytes());

        try (MockedStatic<RuoYiConfig> ruoyiConfigMock = Mockito.mockStatic(RuoYiConfig.class);
             MockedStatic<FileUploadUtils> fileUploadUtilsMock = Mockito.mockStatic(FileUploadUtils.class)) {

            ruoyiConfigMock.when(RuoYiConfig::getUploadPath).thenReturn("/profile/upload");
            fileUploadUtilsMock.when(() -> FileUploadUtils.upload(anyString(), any(MultipartFile.class), any()))
                .thenThrow(new IOException("模拟上传失败"));

            mockMvc.perform(multipart("/system/videoresource/uploadThumbnail").file(file).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.ERROR))
                .andExpect(jsonPath("$.msg").value("模拟上传失败"));
        }
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "add")
    @DisplayName("测试新增视频资源 - 成功")
    void testAdd_Success() throws Exception {
        VideoResource videoResource = createTestVideoResource();
        when(videoResourceService.insertVideoResource(any(VideoResource.class))).thenReturn(1);

        mockMvc.perform(post("/system/videoresource").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(videoResource)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));
        verify(videoResourceService).insertVideoResource(any(VideoResource.class));
    }
    
    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "add")
    @DisplayName("测试新增视频资源 - 使用默认缩略图")
    void testAdd_DefaultThumbnail() throws Exception {
        VideoResource videoResource = createTestVideoResource();
        videoResource.setThumbnail(null); // Explicitly set thumbnail to null

        ArgumentCaptor<VideoResource> captor = ArgumentCaptor.forClass(VideoResource.class);
        when(videoResourceService.insertVideoResource(captor.capture())).thenReturn(1);

        mockMvc.perform(post("/system/videoresource").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(videoResource)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));
        
        assertEquals("/profile/default/video-thumbnail.png", captor.getValue().getThumbnail());
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "add")
    @DisplayName("测试新增视频资源 - 失败")
    void testAdd_Failure() throws Exception {
        VideoResource videoResource = createTestVideoResource();
        when(videoResourceService.insertVideoResource(any(VideoResource.class))).thenReturn(0);

        mockMvc.perform(post("/system/videoresource").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(videoResource)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.ERROR));
        verify(videoResourceService).insertVideoResource(any(VideoResource.class));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "edit")
    @DisplayName("测试修改视频资源 - 成功")
    void testEdit_Success() throws Exception {
        VideoResource videoResource = createTestVideoResource();
        when(videoResourceService.updateVideoResource(any(VideoResource.class))).thenReturn(1);

        mockMvc.perform(put("/system/videoresource").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(videoResource)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));
        verify(videoResourceService).updateVideoResource(any(VideoResource.class));
    }
    
    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "edit")
    @DisplayName("测试修改视频资源 - 失败")
    void testEdit_Failure() throws Exception {
        VideoResource videoResource = createTestVideoResource();
        when(videoResourceService.updateVideoResource(any(VideoResource.class))).thenReturn(0);

        mockMvc.perform(put("/system/videoresource").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(videoResource)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.ERROR));
        verify(videoResourceService).updateVideoResource(any(VideoResource.class));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "remove")
    @DisplayName("测试删除视频资源 - 成功")
    void testRemove_Success() throws Exception {
        Long[] videoIds = {1L, 2L};
        when(videoResourceService.deleteVideoResourceByVideoIds(videoIds)).thenReturn(2);

        mockMvc.perform(delete("/system/videoresource/{videoIds}", "1,2").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));
        verify(videoResourceService).deleteVideoResourceByVideoIds(videoIds);
    }
    
    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "remove")
    @DisplayName("测试删除视频资源 - 失败")
    void testRemove_Failure() throws Exception {
        Long[] videoIds = {99L};
        when(videoResourceService.deleteVideoResourceByVideoIds(videoIds)).thenReturn(0);

        mockMvc.perform(delete("/system/videoresource/{videoIds}", "99").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.ERROR));
        verify(videoResourceService).deleteVideoResourceByVideoIds(videoIds);
    }
} 