package com.ruoyi.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.system.domain.StudentSkill;
import com.ruoyi.system.service.IStudentSkillService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * StudentSkillController 的单元测试类
 *
 * @see com.ruoyi.web.controller.StudentSkillController
 */
@WebMvcTest(StudentSkillController.class)
@DisplayName("学生能力Controller测试")
class StudentSkillControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IStudentSkillService studentSkillService;

    //--- 模拟框架依赖 ---
    @MockBean
    private RedisCache redisCache;
    @MockBean
    private TokenService tokenService;

    private static final String MOCK_USER = "testUser";
    private static final String PERMISSION_PREFIX = "system:skill:";

    // --- Standard CRUD & Export ---

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "list")
    @DisplayName("测试查询学生能力列表 - 成功")
    void testList_Success() throws Exception {
        when(studentSkillService.selectStudentSkillList(any(StudentSkill.class)))
                .thenReturn(Collections.singletonList(new StudentSkill()));
        mockMvc.perform(get("/system/skill/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "export")
    @DisplayName("测试导出学生能力列表 - 成功")
    void testExport_Success() throws Exception {
        when(studentSkillService.selectStudentSkillList(any(StudentSkill.class)))
                .thenReturn(Collections.emptyList());
        mockMvc.perform(post("/system/skill/export").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "query")
    @DisplayName("测试根据ID获取学生能力 - 成功")
    void testGetInfo_Success() throws Exception {
        long id = 1L;
        StudentSkill skill = new StudentSkill();
        skill.setId(id);
        when(studentSkillService.selectStudentSkillById(id)).thenReturn(skill);
        mockMvc.perform(get("/system/skill/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(id));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "add")
    @DisplayName("测试新增学生能力 - 成功")
    void testAdd_Success() throws Exception {
        StudentSkill skill = new StudentSkill();
        when(studentSkillService.insertStudentSkill(any(StudentSkill.class))).thenReturn(1);
        mockMvc.perform(post("/system/skill").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(skill)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "edit")
    @DisplayName("测试修改学生能力 - 成功")
    void testEdit_Success() throws Exception {
        StudentSkill skill = new StudentSkill();
        when(studentSkillService.updateStudentSkill(any(StudentSkill.class))).thenReturn(1);
        mockMvc.perform(put("/system/skill").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(skill)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "remove")
    @DisplayName("测试删除学生能力 - 成功")
    void testRemove_Success() throws Exception {
        Long[] ids = {1L, 2L};
        when(studentSkillService.deleteStudentSkillByIds(ids)).thenReturn(2);
        mockMvc.perform(delete("/system/skill/{ids}", "1,2").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));
    }

    // --- Custom Business Logic ---

    @Test
    @WithMockUser(username = MOCK_USER)
    @DisplayName("测试根据学生和课程ID查询能力 - 成功")
    void testGetStudentSkillByStudentAndCourse_Success() throws Exception {
        long studentId = 1L;
        long courseId = 1L;
        List<StudentSkill> skillList = Collections.singletonList(new StudentSkill());
        when(studentSkillService.selectStudentSkillByStudentAndCourse(studentId, courseId)).thenReturn(skillList);

        mockMvc.perform(get("/system/skill/student/{studentId}/course/{courseId}", studentId, courseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    @WithMockUser(username = MOCK_USER)
    @DisplayName("测试初始化学生能力 - 成功")
    void testInitStudentCourseSkills_Success() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("studentId", 1L);
        params.put("courseId", 1L);

        when(studentSkillService.initStudentCourseSkills(1L, 1L)).thenReturn(1);

        mockMvc.perform(post("/system/skill/init").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));
    }

    @Test
    @WithMockUser(username = MOCK_USER)
    @DisplayName("测试初始化学生能力 - 参数为空对象")
    void testInitStudentCourseSkills_EmptyParams() throws Exception {
        // 已修正：发送一个空的JSON对象 {} 来触发内部的 .get() == null 判断
        mockMvc.perform(post("/system/skill/init").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new HashMap<>())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("参数不能为空：studentId和courseId"));
    }

    @Test
    @WithMockUser(username = MOCK_USER)
    @DisplayName("测试初始化学生能力 - 缺少studentId")
    void testInitStudentCourseSkills_MissingStudentId() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("courseId", 1L);

        mockMvc.perform(post("/system/skill/init").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("参数不能为空：studentId和courseId"));
    }

    @Test
    @WithMockUser(username = MOCK_USER)
    @DisplayName("测试初始化学生能力 - 参数无效")
    void testInitStudentCourseSkills_InvalidParams() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("studentId", 0L);
        params.put("courseId", 1L);

        mockMvc.perform(post("/system/skill/init").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("参数无效：studentId和courseId必须大于0"));
    }

    @Test
    @WithMockUser(username = MOCK_USER)
    @DisplayName("测试初始化学生能力 - 参数格式错误")
    void testInitStudentCourseSkills_NumberFormatException() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("studentId", "abc");
        params.put("courseId", 1L);

        mockMvc.perform(post("/system/skill/init").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("参数格式错误：studentId和courseId必须是数字"));
    }

    @Test
    @WithMockUser(username = MOCK_USER)
    @DisplayName("测试初始化学生能力 - Service层异常")
    void testInitStudentCourseSkills_ServiceException() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("studentId", 1L);
        params.put("courseId", 1L);

        String errorMessage = "数据库连接失败";
        when(studentSkillService.initStudentCourseSkills(1L, 1L)).thenThrow(new RuntimeException(errorMessage));

        mockMvc.perform(post("/system/skill/init").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("初始化失败：" + errorMessage));
    }
}