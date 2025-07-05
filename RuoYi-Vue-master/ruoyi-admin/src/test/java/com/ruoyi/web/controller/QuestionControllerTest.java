package com.ruoyi.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.system.domain.Question;
import com.ruoyi.system.domain.TestPaper;
import com.ruoyi.system.domain.vo.PaperGenerateRequest;
import com.ruoyi.system.service.IPaperGenerateService;
import com.ruoyi.system.service.IQuestionService;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * QuestionController 的单元测试类
 *
 * @see com.ruoyi.web.controller.QuestionController
 */
@WebMvcTest(QuestionController.class)
@DisplayName("题目Controller测试")
class QuestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IQuestionService questionService;
    @MockBean
    private IPaperGenerateService paperGenerateService;

    //--- 模拟框架依赖 ---
    @MockBean
    private RedisCache redisCache;
    @MockBean
    private TokenService tokenService;

    private static final String MOCK_USER = "testUser";
    private static final String PERMISSION_PREFIX = "system:question:";

    // --- Standard CRUD & Export ---

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "list")
    @DisplayName("测试查询题目列表 - 成功")
    void testList_Success() throws Exception {
        when(questionService.selectQuestionList(any(Question.class)))
                .thenReturn(Collections.singletonList(new Question()));
        mockMvc.perform(get("/system/question/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "export")
    @DisplayName("测试导出题目列表 - 成功")
    void testExport_Success() throws Exception {
        when(questionService.selectQuestionList(any(Question.class)))
                .thenReturn(Collections.emptyList());
        mockMvc.perform(post("/system/question/export").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8"));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "query")
    @DisplayName("测试根据ID获取题目 - 成功")
    void testGetInfo_Success() throws Exception {
        long questionId = 1L;
        Question question = new Question();
        question.setQuestionId(questionId);
        when(questionService.selectQuestionByQuestionId(questionId)).thenReturn(question);
        mockMvc.perform(get("/system/question/{questionId}", questionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.questionId").value(questionId));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "query")
    @DisplayName("测试根据ID获取题目 - 未找到")
    void testGetInfo_NotFound() throws Exception {
        when(questionService.selectQuestionByQuestionId(anyLong())).thenReturn(null);
        mockMvc.perform(get("/system/question/{questionId}", 99L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "add")
    @DisplayName("测试新增题目 - 成功")
    void testAdd_Success() throws Exception {
        Question question = new Question();
        when(questionService.insertQuestion(any(Question.class))).thenReturn(1);
        mockMvc.perform(post("/system/question").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(question)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "add")
    @DisplayName("测试新增题目 - 失败")
    void testAdd_Failure() throws Exception {
        when(questionService.insertQuestion(any(Question.class))).thenReturn(0);
        mockMvc.perform(post("/system/question").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Question())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.ERROR));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "edit")
    @DisplayName("测试修改题目 - 成功")
    void testEdit_Success() throws Exception {
        Question question = new Question();
        when(questionService.updateQuestion(any(Question.class))).thenReturn(1);
        mockMvc.perform(put("/system/question").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(question)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "edit")
    @DisplayName("测试修改题目 - 失败")
    void testEdit_Failure() throws Exception {
        when(questionService.updateQuestion(any(Question.class))).thenReturn(0);
        mockMvc.perform(put("/system/question").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Question())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.ERROR));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "remove")
    @DisplayName("测试删除题目 - 成功")
    void testRemove_Success() throws Exception {
        Long[] ids = {1L, 2L};
        when(questionService.deleteQuestionByQuestionIds(ids)).thenReturn(2);
        mockMvc.perform(delete("/system/question/{questionIds}", "1,2").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "remove")
    @DisplayName("测试删除题目 - 失败")
    void testRemove_Failure() throws Exception {
        Long[] ids = {99L};
        when(questionService.deleteQuestionByQuestionIds(ids)).thenReturn(0);
        mockMvc.perform(delete("/system/question/{questionIds}", "99").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.ERROR));
    }

    // --- Custom Business Logic ---

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "list")
    @DisplayName("测试根据课程ID获取题目列表 - 成功")
    void testGetQuestionsByCourseId_Success() throws Exception {
        long courseId = 1L;
        Question question = new Question();
        question.setQuestionId(101L);
        // 已修正：删除了不存在的 setCourseId 调用
        List<Question> questionList = Collections.singletonList(question);

        when(questionService.selectQuestionsByCourseId(courseId)).thenReturn(questionList);

        mockMvc.perform(get("/system/question/course/{courseId}", courseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS))
                .andExpect(jsonPath("$.data.length()").value(1))
                // 已修正：验证一个真实存在的字段，如 questionId
                .andExpect(jsonPath("$.data[0].questionId").value(101L));

        verify(questionService).selectQuestionsByCourseId(courseId);
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "generate")
    @DisplayName("测试生成试卷 - 成功")
    void testGeneratePaper_Success() throws Exception {
        PaperGenerateRequest request = new PaperGenerateRequest();
        TestPaper paper = new TestPaper();
        paper.setPaperId(1L);
        paper.setPaperName("生成的测试试卷");

        when(paperGenerateService.generatePaper(any(PaperGenerateRequest.class))).thenReturn(paper);

        mockMvc.perform(post("/system/question/generate").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS))
                .andExpect(jsonPath("$.data.paperName").value("生成的测试试卷"));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "generate")
    @DisplayName("测试生成试卷 - 失败(服务层异常)")
    void testGeneratePaper_Failure() throws Exception {
        PaperGenerateRequest request = new PaperGenerateRequest();
        String errorMessage = "题库题目不足，无法生成试卷";

        when(paperGenerateService.generatePaper(any(PaperGenerateRequest.class)))
                .thenThrow(new RuntimeException(errorMessage));

        mockMvc.perform(post("/system/question/generate").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.ERROR))
                .andExpect(jsonPath("$.msg").value(errorMessage));
    }
}