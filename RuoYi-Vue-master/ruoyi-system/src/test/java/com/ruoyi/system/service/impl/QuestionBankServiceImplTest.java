package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.QuestionBank;
import com.ruoyi.system.mapper.QuestionBankMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * QuestionBankServiceImpl 单元测试
 */
@ExtendWith(MockitoExtension.class)
class QuestionBankServiceImplTest {

    @Mock private QuestionBankMapper mapper;
    @InjectMocks private QuestionBankServiceImpl service;
    private QuestionBank bank;

    @BeforeEach
    void init() {
        bank = new QuestionBank();
        bank.setBankId(1L);
    }

    @Test
    void insert_setsCreateTime() {
        when(mapper.insertQuestionBank(any())).thenReturn(1);
        assertEquals(1, service.insertQuestionBank(bank));
        assertNotNull(bank.getCreateTime());
    }

    @Test
    void update_setsUpdateTime() {
        when(mapper.updateQuestionBank(any())).thenReturn(1);
        assertEquals(1, service.updateQuestionBank(bank));
        assertNotNull(bank.getUpdateTime());
    }

    @Test
    void selectDelete_delegate() {
        when(mapper.selectQuestionBankByBankId(1L)).thenReturn(bank);
        when(mapper.selectQuestionBankList(any())).thenReturn(Collections.singletonList(bank));
        when(mapper.deleteQuestionBankByBankIds(any())).thenReturn(2);
        when(mapper.deleteQuestionBankByBankId(1L)).thenReturn(1);

        assertEquals(bank, service.selectQuestionBankByBankId(1L));
        assertEquals(1, service.selectQuestionBankList(new QuestionBank()).size());
        assertEquals(2, service.deleteQuestionBankByBankIds(new Long[]{1L,2L}));
        assertEquals(1, service.deleteQuestionBankByBankId(1L));
    }
    @Test
    void selectById_mapperReturnsNull_shouldReturnNull() {
        when(mapper.selectQuestionBankByBankId(99L)).thenReturn(null);
        assertNull(service.selectQuestionBankByBankId(99L));
    }

    @Test
    void selectList_mapperReturnsNull_shouldReturnNull() {
        when(mapper.selectQuestionBankList(any())).thenReturn(null);
        assertNull(service.selectQuestionBankList(new QuestionBank()));
    }

    @Test
    void deleteByBankIds_emptyArray_shouldReturnZero() {
        when(mapper.deleteQuestionBankByBankIds(new Long[0])).thenReturn(0);
        assertEquals(0, service.deleteQuestionBankByBankIds(new Long[0]));
    }

    @Test
    void allMapperMethods_throwException_shouldPropagate() {
        RuntimeException ex = new RuntimeException("db error");
        when(mapper.selectQuestionBankByBankId(anyLong())).thenThrow(ex);
        when(mapper.selectQuestionBankList(any())).thenThrow(ex);
        when(mapper.insertQuestionBank(any())).thenThrow(ex);
        when(mapper.updateQuestionBank(any())).thenThrow(ex);
        when(mapper.deleteQuestionBankByBankIds(any())).thenThrow(ex);
        when(mapper.deleteQuestionBankByBankId(anyLong())).thenThrow(ex);

        assertThrows(RuntimeException.class, () -> service.selectQuestionBankByBankId(1L));
        assertThrows(RuntimeException.class, () -> service.selectQuestionBankList(new QuestionBank()));
        assertThrows(RuntimeException.class, () -> service.insertQuestionBank(bank));
        assertThrows(RuntimeException.class, () -> service.updateQuestionBank(bank));
        assertThrows(RuntimeException.class, () -> service.deleteQuestionBankByBankIds(new Long[]{1L}));
        assertThrows(RuntimeException.class, () -> service.deleteQuestionBankByBankId(1L));
    }
} 