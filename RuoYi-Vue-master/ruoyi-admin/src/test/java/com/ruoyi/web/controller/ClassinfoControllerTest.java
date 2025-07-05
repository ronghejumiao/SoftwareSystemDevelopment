package com.ruoyi.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.system.domain.Classinfo;
import com.ruoyi.system.service.IClassinfoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf; // <--- IMPORT THIS
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * ClassinfoController 的单元测试类
 *
 * @see com.ruoyi.web.controller.ClassinfoController
 */
@WebMvcTest(ClassinfoController.class)
@DisplayName("班级信息Controller测试")
class ClassinfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IClassinfoService classinfoService;
    @MockBean
    private RedisCache redisCache;
    @MockBean
    private TokenService tokenService;

    private static final String MOCK_USER = "testUser";
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = {"system:classinfo:list"})
    @DisplayName("测试查询班级列表 - 成功")
    void testList_Success() throws Exception {
        // GET requests do not need CSRF
        List<Classinfo> list = Collections.singletonList(new Classinfo());
        when(classinfoService.selectClassinfoList(any(Classinfo.class))).thenReturn(list);
        mockMvc.perform(get("/system/classinfo/list"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = {"system:classinfo:export"})
    @DisplayName("测试导出班级列表 - 成功")
    void testExport_Success() throws Exception {
        when(classinfoService.selectClassinfoList(any(Classinfo.class))).thenReturn(Collections.singletonList(new Classinfo()));
        mockMvc.perform(post("/system/classinfo/export").with(csrf())) // <-- Add CSRF
                .andExpect(status().isOk());
    }


    @Test
    @WithMockUser(username = MOCK_USER, authorities = {"system:classinfo:query"})
    @DisplayName("测试根据ID获取班级信息 - 成功")
    void testGetInfo_Success() throws Exception {
        // GET requests do not need CSRF
        Long classId = 1L;
        Classinfo classinfo = new Classinfo();
        classinfo.setClassId(classId);
        when(classinfoService.selectClassinfoByClassId(classId)).thenReturn(classinfo);
        mockMvc.perform(get("/system/classinfo/{classId}", classId))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = {"system:classinfo:add"})
    @DisplayName("测试新增班级 - 成功")
    void testAdd_Success() throws Exception {
        Classinfo classinfo = new Classinfo();
        classinfo.setClassName("新增测试班级");
        when(classinfoService.insertClassinfo(any(Classinfo.class))).thenReturn(1);

        mockMvc.perform(post("/system/classinfo").with(csrf()) // <-- Add CSRF
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(classinfo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = {"system:classinfo:add"})
    @DisplayName("测试新增班级 - 失败")
    void testAdd_Failure() throws Exception {
        Classinfo classinfo = new Classinfo();
        classinfo.setClassName("新增失败班级");
        when(classinfoService.insertClassinfo(any(Classinfo.class))).thenReturn(0);

        mockMvc.perform(post("/system/classinfo").with(csrf()) // <-- Add CSRF
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(classinfo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.ERROR));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = {"system:classinfo:edit"})
    @DisplayName("测试修改班级 - 成功")
    void testEdit_Success() throws Exception {
        Classinfo classinfo = new Classinfo();
        classinfo.setClassId(1L);
        when(classinfoService.updateClassinfo(any(Classinfo.class))).thenReturn(1);

        mockMvc.perform(put("/system/classinfo").with(csrf()) // <-- Add CSRF
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(classinfo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = {"system:classinfo:edit"})
    @DisplayName("测试修改班级 - 失败")
    void testEdit_Failure() throws Exception {
        Classinfo classinfo = new Classinfo();
        classinfo.setClassId(1L);
        when(classinfoService.updateClassinfo(any(Classinfo.class))).thenReturn(0);

        mockMvc.perform(put("/system/classinfo").with(csrf()) // <-- Add CSRF
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(classinfo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.ERROR));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = {"system:classinfo:remove"})
    @DisplayName("测试删除班级 - 成功")
    void testRemove_Success() throws Exception {
        Long[] classIds = {1L, 2L};
        when(classinfoService.deleteClassinfoByClassIds(classIds)).thenReturn(classIds.length);

        mockMvc.perform(delete("/system/classinfo/{classIds}", "1,2").with(csrf())) // <-- Add CSRF
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = {"system:classinfo:remove"})
    @DisplayName("测试删除班级 - 失败")
    void testRemove_Failure() throws Exception {
        Long[] classIds = {99L};
        when(classinfoService.deleteClassinfoByClassIds(classIds)).thenReturn(0);

        mockMvc.perform(delete("/system/classinfo/{classIds}", "99").with(csrf())) // <-- Add CSRF
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.ERROR));
    }
}