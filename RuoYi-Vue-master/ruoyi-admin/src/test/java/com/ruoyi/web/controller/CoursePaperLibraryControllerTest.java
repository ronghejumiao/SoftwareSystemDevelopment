package com.ruoyi.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.system.domain.CoursePaperLibrary;
import com.ruoyi.system.service.ICoursePaperLibraryService;
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
 * [cite_start]CoursePaperLibraryController 的单元测试类 [cite: 1]
 *
 * @see com.ruoyi.web.controller.CoursePaperLibraryController
 */
@WebMvcTest(CoursePaperLibraryController.class)
@DisplayName("课程试卷库Controller测试")
class CoursePaperLibraryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ICoursePaperLibraryService coursePaperLibraryService;

    //--- Mock框架依赖 ---
    @MockBean
    private RedisCache redisCache;
    @MockBean
    private TokenService tokenService;

    private static final String MOCK_USER = "testUser";
    private static final String PERMISSION_PREFIX = "system:library:";

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "list")
    @DisplayName("测试查询课程试卷库列表 - 成功")
    void testList_Success() throws Exception {
        when(coursePaperLibraryService.selectCoursePaperLibraryList(any(CoursePaperLibrary.class)))
                .thenReturn(Collections.singletonList(new CoursePaperLibrary()));

        mockMvc.perform(get("/system/library/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1));

        verify(coursePaperLibraryService).selectCoursePaperLibraryList(any(CoursePaperLibrary.class));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "export")
    @DisplayName("测试导出课程试卷库列表 - 成功")
    void testExport_Success() throws Exception {
        when(coursePaperLibraryService.selectCoursePaperLibraryList(any(CoursePaperLibrary.class)))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(post("/system/library/export").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8"));

        verify(coursePaperLibraryService).selectCoursePaperLibraryList(any(CoursePaperLibrary.class));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "query")
    @DisplayName("测试根据ID获取课程试卷库 - 成功")
    void testGetInfo_Success() throws Exception {
        long libraryId = 1L;
        CoursePaperLibrary library = new CoursePaperLibrary();
        library.setLibraryId(libraryId);

        when(coursePaperLibraryService.selectCoursePaperLibraryByLibraryId(libraryId)).thenReturn(library);

        mockMvc.perform(get("/system/library/{libraryId}", libraryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS))
                .andExpect(jsonPath("$.data.libraryId").value(libraryId));

        verify(coursePaperLibraryService).selectCoursePaperLibraryByLibraryId(libraryId);
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "query")
    @DisplayName("测试根据ID获取课程试卷库 - 未找到")
    void testGetInfo_NotFound() throws Exception {
        long libraryId = 99L;
        when(coursePaperLibraryService.selectCoursePaperLibraryByLibraryId(libraryId)).thenReturn(null);

        mockMvc.perform(get("/system/library/{libraryId}", libraryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(coursePaperLibraryService).selectCoursePaperLibraryByLibraryId(libraryId);
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "add")
    @DisplayName("测试新增课程试卷库 - 成功")
    void testAdd_Success() throws Exception {
        CoursePaperLibrary library = new CoursePaperLibrary();
        library.setCourseId(1L);

        when(coursePaperLibraryService.insertCoursePaperLibrary(any(CoursePaperLibrary.class))).thenReturn(1);

        mockMvc.perform(post("/system/library").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(library)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));

        verify(coursePaperLibraryService).insertCoursePaperLibrary(any(CoursePaperLibrary.class));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "add")
    @DisplayName("测试新增课程试卷库 - 失败")
    void testAdd_Failure() throws Exception {
        CoursePaperLibrary library = new CoursePaperLibrary();
        when(coursePaperLibraryService.insertCoursePaperLibrary(any(CoursePaperLibrary.class))).thenReturn(0);

        mockMvc.perform(post("/system/library").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(library)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.ERROR));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "edit")
    @DisplayName("测试修改课程试卷库 - 成功")
    void testEdit_Success() throws Exception {
        CoursePaperLibrary library = new CoursePaperLibrary();
        library.setLibraryId(1L);

        when(coursePaperLibraryService.updateCoursePaperLibrary(any(CoursePaperLibrary.class))).thenReturn(1);

        mockMvc.perform(put("/system/library").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(library)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));

        verify(coursePaperLibraryService).updateCoursePaperLibrary(any(CoursePaperLibrary.class));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "edit")
    @DisplayName("测试修改课程试卷库 - 失败")
    void testEdit_Failure() throws Exception {
        CoursePaperLibrary library = new CoursePaperLibrary();
        library.setLibraryId(1L);
        when(coursePaperLibraryService.updateCoursePaperLibrary(any(CoursePaperLibrary.class))).thenReturn(0);

        mockMvc.perform(put("/system/library").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(library)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.ERROR));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "remove")
    @DisplayName("测试删除课程试卷库 - 成功")
    void testRemove_Success() throws Exception {
        Long[] libraryIds = {1L, 2L};
        when(coursePaperLibraryService.deleteCoursePaperLibraryByLibraryIds(libraryIds)).thenReturn(libraryIds.length);

        mockMvc.perform(delete("/system/library/{libraryIds}", "1,2").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));

        verify(coursePaperLibraryService).deleteCoursePaperLibraryByLibraryIds(libraryIds);
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "remove")
    @DisplayName("测试删除课程试卷库 - 失败")
    void testRemove_Failure() throws Exception {
        Long[] libraryIds = {99L};
        when(coursePaperLibraryService.deleteCoursePaperLibraryByLibraryIds(libraryIds)).thenReturn(0);

        mockMvc.perform(delete("/system/library/{libraryIds}", "99").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.ERROR));
    }
}