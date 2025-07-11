package com.ruoyi.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.system.domain.CourseImg;
import com.ruoyi.system.service.ICourseImgService;
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
 * CourseImgController 的单元测试类
 *
 * @see com.ruoyi.web.controller.CourseImgController
 */
@WebMvcTest(CourseImgController.class)
@DisplayName("课程概念图Controller测试")
class CourseImgControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ICourseImgService courseImgService;
    @MockBean
    private RedisCache redisCache;
    @MockBean
    private TokenService tokenService;

    private static final String MOCK_USER = "testUser";
    private static final String PERMISSION_PREFIX = "system:img:";

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "list")
    @DisplayName("测试查询课程概念图列表 - 成功")
    void testList_Success() throws Exception {
        when(courseImgService.selectCourseImgList(any(CourseImg.class)))
                .thenReturn(Collections.singletonList(new CourseImg()));

        mockMvc.perform(get("/system/img/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1));

        verify(courseImgService).selectCourseImgList(any(CourseImg.class));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "export")
    @DisplayName("测试导出课程概念图列表 - 成功")
    void testExport_Success() throws Exception {
        when(courseImgService.selectCourseImgList(any(CourseImg.class)))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(post("/system/img/export").with(csrf()))
                .andExpect(status().isOk())
                // 已修正：不再检查不确定的header, 而是检查确定的Content-Type
                .andExpect(content().contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8"));

        verify(courseImgService).selectCourseImgList(any(CourseImg.class));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "query")
    @DisplayName("测试根据ID获取课程概念图 - 成功")
    void testGetInfo_Success() throws Exception {
        long mapId = 1L;
        CourseImg courseImg = new CourseImg();
        courseImg.setMapId(mapId);
        courseImg.setMapUrl("http://example.com/img.png");

        when(courseImgService.selectCourseImgByMapId(mapId)).thenReturn(courseImg);

        mockMvc.perform(get("/system/img/{mapId}", mapId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS))
                .andExpect(jsonPath("$.data.mapId").value(mapId));

        verify(courseImgService).selectCourseImgByMapId(mapId);
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "query")
    @DisplayName("测试根据ID获取课程概念图 - 未找到")
    void testGetInfo_NotFound() throws Exception {
        long mapId = 99L;
        when(courseImgService.selectCourseImgByMapId(mapId)).thenReturn(null);

        mockMvc.perform(get("/system/img/{mapId}", mapId))
                .andExpect(status().isOk())
                // 已修正: 断言data字段不存在，而不是为空
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(courseImgService).selectCourseImgByMapId(mapId);
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "add")
    @DisplayName("测试新增课程概念图 - 成功")
    void testAdd_Success() throws Exception {
        CourseImg courseImg = new CourseImg();
        courseImg.setCourseId(1L);
        courseImg.setMapUrl("http://example.com/new.png");

        when(courseImgService.insertCourseImg(any(CourseImg.class))).thenReturn(1);

        mockMvc.perform(post("/system/img").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseImg)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));

        verify(courseImgService).insertCourseImg(any(CourseImg.class));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "edit")
    @DisplayName("测试修改课程概念图 - 成功")
    void testEdit_Success() throws Exception {
        CourseImg courseImg = new CourseImg();
        courseImg.setMapId(1L);
        courseImg.setMapUrl("http://example.com/updated.png");

        when(courseImgService.updateCourseImg(any(CourseImg.class))).thenReturn(1);

        mockMvc.perform(put("/system/img").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseImg)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));

        verify(courseImgService).updateCourseImg(any(CourseImg.class));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "edit")
    @DisplayName("测试修改课程概念图 - 失败")
    void testEdit_Failure() throws Exception {
        CourseImg courseImg = new CourseImg();
        courseImg.setMapId(1L);

        when(courseImgService.updateCourseImg(any(CourseImg.class))).thenReturn(0);

        mockMvc.perform(put("/system/img").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseImg)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.ERROR));

        verify(courseImgService).updateCourseImg(any(CourseImg.class));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "remove")
    @DisplayName("测试删除课程概念图 - 成功")
    void testRemove_Success() throws Exception {
        Long[] mapIds = {1L, 2L};
        when(courseImgService.deleteCourseImgByMapIds(mapIds)).thenReturn(mapIds.length);

        mockMvc.perform(delete("/system/img/{mapIds}", "1,2").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));

        verify(courseImgService).deleteCourseImgByMapIds(mapIds);
    }
}