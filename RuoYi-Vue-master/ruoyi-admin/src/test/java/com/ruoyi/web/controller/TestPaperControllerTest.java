package com.ruoyi.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.system.domain.TestPaper;
import com.ruoyi.system.domain.vo.PaperGenerateRequest;
import com.ruoyi.system.service.ITestPaperService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * TestPaperController 的单元测试类
 *
 * @see com.ruoyi.web.controller.TestPaperController
 */
@WebMvcTest(TestPaperController.class)
@DisplayName("试卷Controller测试")
class TestPaperControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ITestPaperService testPaperService;

    //--- 模拟框架依赖 ---
    @MockBean
    private RedisCache redisCache;
    @MockBean
    private TokenService tokenService;

    private static final String MOCK_USER = "testUser";
    private static final String PERMISSION_PREFIX = "system:paper:";

    // --- Standard CRUD & Export ---

    @Test
    @WithMockUser(username = MOCK_USER)
    @DisplayName("测试查询试卷列表 - 成功")
    void testList_Success() throws Exception {
        when(testPaperService.selectTestPaperList(any(TestPaper.class)))
                .thenReturn(Collections.singletonList(new TestPaper()));
        mockMvc.perform(get("/system/paper/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "export")
    @DisplayName("测试导出试卷列表 - 成功")
    void testExport_Success() throws Exception {
        when(testPaperService.selectTestPaperList(any(TestPaper.class)))
                .thenReturn(Collections.emptyList());
        mockMvc.perform(post("/system/paper/export").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8"));
    }

    @Test
    @WithMockUser(username = MOCK_USER)
    @DisplayName("测试根据ID获取试卷 - 成功")
    void testGetInfo_Success() throws Exception {
        long paperId = 1L;
        TestPaper paper = new TestPaper();
        paper.setPaperId(paperId);
        when(testPaperService.selectTestPaperByPaperId(paperId)).thenReturn(paper);
        mockMvc.perform(get("/system/paper/{paperId}", paperId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.paperId").value(paperId));
    }

    @Test
    @WithMockUser(username = MOCK_USER)
    @DisplayName("测试根据ID获取试卷 - 未找到")
    void testGetInfo_NotFound() throws Exception {
        when(testPaperService.selectTestPaperByPaperId(anyLong())).thenReturn(null);
        mockMvc.perform(get("/system/paper/{paperId}", 99L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "add")
    @DisplayName("测试新增试卷 - 成功")
    void testAdd_Success() throws Exception {
        TestPaper paper = new TestPaper();
        when(testPaperService.insertTestPaper(any(TestPaper.class))).thenReturn(1);
        mockMvc.perform(post("/system/paper").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paper)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "edit")
    @DisplayName("测试修改试卷 - 成功")
    void testEdit_Success() throws Exception {
        TestPaper paper = new TestPaper();
        when(testPaperService.updateTestPaper(any(TestPaper.class))).thenReturn(1);
        mockMvc.perform(put("/system/paper").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paper)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "remove")
    @DisplayName("测试删除试卷 - 成功")
    void testRemove_Success() throws Exception {
        Long[] ids = {1L, 2L};
        when(testPaperService.deleteTestPaperByPaperIds(ids)).thenReturn(2);
        mockMvc.perform(delete("/system/paper/{paperIds}", "1,2").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));
    }

    // --- Custom Business Logic ---

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "add")
    @DisplayName("测试生成试卷 - 成功")
    void testGeneratePaper_Success() throws Exception {
        PaperGenerateRequest request = new PaperGenerateRequest();
        TestPaper paper = new TestPaper();
        paper.setPaperId(1L);
        paper.setPaperName("生成的测试试卷");

        when(testPaperService.generatePaper(any(PaperGenerateRequest.class))).thenReturn(paper);

        mockMvc.perform(post("/system/paper/generate").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS))
                .andExpect(jsonPath("$.data.paperName").value("生成的测试试卷"));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "add")
    @DisplayName("测试生成试卷 - 失败(服务层异常)")
    void testGeneratePaper_Failure() throws Exception {
        PaperGenerateRequest request = new PaperGenerateRequest();
        // 异常消息从 "题目不足" 修改为 "试卷生成失败：题目不足"
        String exceptionMessage = "题目不足";
        String expectedMessage = "试卷生成失败：" + exceptionMessage;

        when(testPaperService.generatePaper(any(PaperGenerateRequest.class)))
                .thenThrow(new RuntimeException(exceptionMessage));

        mockMvc.perform(post("/system/paper/generate").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.ERROR))
                .andExpect(jsonPath("$.msg").value(expectedMessage)); // 验证拼接后的完整消息
    }

    @Test
    @WithMockUser(username = MOCK_USER)
    @DisplayName("测试根据课程ID查询试卷列表 - 成功")
    void testListByCourseId_Success() throws Exception {
        long courseId = 1L;
        ArgumentCaptor<TestPaper> captor = ArgumentCaptor.forClass(TestPaper.class);
        when(testPaperService.selectTestPaperList(captor.capture()))
                .thenReturn(Collections.singletonList(new TestPaper()));

        mockMvc.perform(get("/system/paper/course/{courseId}", courseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS))
                .andExpect(jsonPath("$.data.length()").value(1));

        assertEquals(courseId, captor.getValue().getCourseId());
    }
}