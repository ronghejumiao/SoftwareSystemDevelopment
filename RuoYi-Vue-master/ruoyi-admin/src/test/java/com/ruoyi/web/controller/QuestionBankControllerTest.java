package com.ruoyi.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.system.domain.QuestionBank;
import com.ruoyi.system.service.IQuestionBankService;
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
 * QuestionBankController 的单元测试类
 *
 * @see com.ruoyi.web.controller.QuestionBankController
 */
@WebMvcTest(QuestionBankController.class)
@DisplayName("题库Controller测试")
class QuestionBankControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IQuestionBankService questionBankService;

    //--- 模拟框架依赖 ---
    @MockBean
    private RedisCache redisCache;
    @MockBean
    private TokenService tokenService;

    private static final String MOCK_USER = "testUser";
    private static final String PERMISSION_PREFIX = "system:bank:";

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "list")
    @DisplayName("测试查询题库列表 - 成功")
    void testList_Success() throws Exception {
        when(questionBankService.selectQuestionBankList(any(QuestionBank.class)))
                .thenReturn(Collections.singletonList(new QuestionBank()));

        mockMvc.perform(get("/system/bank/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1));

        verify(questionBankService).selectQuestionBankList(any(QuestionBank.class));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "export")
    @DisplayName("测试导出题库列表 - 成功")
    void testExport_Success() throws Exception {
        when(questionBankService.selectQuestionBankList(any(QuestionBank.class)))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(post("/system/bank/export").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8"));

        verify(questionBankService).selectQuestionBankList(any(QuestionBank.class));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "query")
    @DisplayName("测试根据ID获取题库 - 成功")
    void testGetInfo_Success() throws Exception {
        long bankId = 1L;
        QuestionBank bank = new QuestionBank();
        bank.setBankId(bankId);

        when(questionBankService.selectQuestionBankByBankId(bankId)).thenReturn(bank);

        mockMvc.perform(get("/system/bank/{bankId}", bankId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS))
                .andExpect(jsonPath("$.data.bankId").value(bankId));

        verify(questionBankService).selectQuestionBankByBankId(bankId);
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "query")
    @DisplayName("测试根据ID获取题库 - 未找到")
    void testGetInfo_NotFound() throws Exception {
        long bankId = 99L;
        when(questionBankService.selectQuestionBankByBankId(bankId)).thenReturn(null);

        mockMvc.perform(get("/system/bank/{bankId}", bankId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(questionBankService).selectQuestionBankByBankId(bankId);
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "add")
    @DisplayName("测试新增题库 - 成功")
    void testAdd_Success() throws Exception {
        QuestionBank bank = new QuestionBank();
        bank.setCourseId(1L);

        when(questionBankService.insertQuestionBank(any(QuestionBank.class))).thenReturn(1);

        mockMvc.perform(post("/system/bank").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bank)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));

        verify(questionBankService).insertQuestionBank(any(QuestionBank.class));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "add")
    @DisplayName("测试新增题库 - 失败")
    void testAdd_Failure() throws Exception {
        QuestionBank bank = new QuestionBank();
        when(questionBankService.insertQuestionBank(any(QuestionBank.class))).thenReturn(0);

        mockMvc.perform(post("/system/bank").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bank)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.ERROR));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "edit")
    @DisplayName("测试修改题库 - 成功")
    void testEdit_Success() throws Exception {
        QuestionBank bank = new QuestionBank();
        bank.setBankId(1L);

        when(questionBankService.updateQuestionBank(any(QuestionBank.class))).thenReturn(1);

        mockMvc.perform(put("/system/bank").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bank)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));

        verify(questionBankService).updateQuestionBank(any(QuestionBank.class));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "edit")
    @DisplayName("测试修改题库 - 失败")
    void testEdit_Failure() throws Exception {
        QuestionBank bank = new QuestionBank();
        bank.setBankId(1L);
        when(questionBankService.updateQuestionBank(any(QuestionBank.class))).thenReturn(0);

        mockMvc.perform(put("/system/bank").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bank)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.ERROR));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "remove")
    @DisplayName("测试删除题库 - 成功")
    void testRemove_Success() throws Exception {
        Long[] bankIds = {1L, 2L};
        when(questionBankService.deleteQuestionBankByBankIds(bankIds)).thenReturn(bankIds.length);

        mockMvc.perform(delete("/system/bank/{bankIds}", "1,2").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));

        verify(questionBankService).deleteQuestionBankByBankIds(bankIds);
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "remove")
    @DisplayName("测试删除题库 - 失败")
    void testRemove_Failure() throws Exception {
        Long[] bankIds = {99L};
        when(questionBankService.deleteQuestionBankByBankIds(bankIds)).thenReturn(0);

        mockMvc.perform(delete("/system/bank/{bankIds}", "99").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.ERROR));
    }
}