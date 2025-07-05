package com.ruoyi.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.system.domain.StudentClass;
import com.ruoyi.system.service.IStudentClassService;
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
 * StudentClassController 的单元测试类
 *
 * @see com.ruoyi.web.controller.StudentClassController
 */
@WebMvcTest(StudentClassController.class)
@DisplayName("学生班级关联Controller测试")
class StudentClassControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IStudentClassService studentClassService;

    //--- 模拟框架依赖 ---
    @MockBean
    private RedisCache redisCache;
    @MockBean
    private TokenService tokenService;

    private static final String MOCK_USER = "testUser";
    private static final String PERMISSION_PREFIX = "system:class:";

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "list")
    @DisplayName("测试查询学生班级关联列表 - 成功")
    void testList_Success() throws Exception {
        when(studentClassService.selectStudentClassList(any(StudentClass.class)))
                .thenReturn(Collections.singletonList(new StudentClass()));

        mockMvc.perform(get("/system/class/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1));

        verify(studentClassService).selectStudentClassList(any(StudentClass.class));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "export")
    @DisplayName("测试导出学生班级关联列表 - 成功")
    void testExport_Success() throws Exception {
        when(studentClassService.selectStudentClassList(any(StudentClass.class)))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(post("/system/class/export").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8"));

        verify(studentClassService).selectStudentClassList(any(StudentClass.class));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "query")
    @DisplayName("测试根据ID获取学生班级关联 - 成功")
    void testGetInfo_Success() throws Exception {
        long id = 1L;
        StudentClass studentClass = new StudentClass();
        studentClass.setId(id);

        when(studentClassService.selectStudentClassById(id)).thenReturn(studentClass);

        mockMvc.perform(get("/system/class/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS))
                .andExpect(jsonPath("$.data.id").value(id));

        verify(studentClassService).selectStudentClassById(id);
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "query")
    @DisplayName("测试根据ID获取学生班级关联 - 未找到")
    void testGetInfo_NotFound() throws Exception {
        long id = 99L;
        when(studentClassService.selectStudentClassById(id)).thenReturn(null);

        mockMvc.perform(get("/system/class/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(studentClassService).selectStudentClassById(id);
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "add")
    @DisplayName("测试新增学生班级关联 - 成功")
    void testAdd_Success() throws Exception {
        StudentClass studentClass = new StudentClass();
        studentClass.setStudentId(1L);
        studentClass.setClassId(1L);

        when(studentClassService.insertStudentClass(any(StudentClass.class))).thenReturn(1);

        mockMvc.perform(post("/system/class").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentClass)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));

        verify(studentClassService).insertStudentClass(any(StudentClass.class));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "add")
    @DisplayName("测试新增学生班级关联 - 失败")
    void testAdd_Failure() throws Exception {
        StudentClass studentClass = new StudentClass();
        when(studentClassService.insertStudentClass(any(StudentClass.class))).thenReturn(0);

        mockMvc.perform(post("/system/class").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentClass)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.ERROR));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "edit")
    @DisplayName("测试修改学生班级关联 - 成功")
    void testEdit_Success() throws Exception {
        StudentClass studentClass = new StudentClass();
        studentClass.setId(1L);

        when(studentClassService.updateStudentClass(any(StudentClass.class))).thenReturn(1);

        mockMvc.perform(put("/system/class").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentClass)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));

        verify(studentClassService).updateStudentClass(any(StudentClass.class));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "edit")
    @DisplayName("测试修改学生班级关联 - 失败")
    void testEdit_Failure() throws Exception {
        StudentClass studentClass = new StudentClass();
        studentClass.setId(1L);
        when(studentClassService.updateStudentClass(any(StudentClass.class))).thenReturn(0);

        mockMvc.perform(put("/system/class").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentClass)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.ERROR));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "remove")
    @DisplayName("测试删除学生班级关联 - 成功")
    void testRemove_Success() throws Exception {
        Long[] ids = {1L, 2L};
        when(studentClassService.deleteStudentClassByIds(ids)).thenReturn(ids.length);

        mockMvc.perform(delete("/system/class/{ids}", "1,2").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));

        verify(studentClassService).deleteStudentClassByIds(ids);
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "remove")
    @DisplayName("测试删除学生班级关联 - 失败")
    void testRemove_Failure() throws Exception {
        Long[] ids = {99L};
        when(studentClassService.deleteStudentClassByIds(ids)).thenReturn(0);

        mockMvc.perform(delete("/system/class/{ids}", "99").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.ERROR));
    }
}