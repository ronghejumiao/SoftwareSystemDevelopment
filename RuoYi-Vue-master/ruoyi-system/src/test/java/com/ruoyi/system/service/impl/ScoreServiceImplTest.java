package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.Score;
import com.ruoyi.system.mapper.ScoreMapper;
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
 * ScoreServiceImpl 单元测试
 */
@ExtendWith(MockitoExtension.class)
class ScoreServiceImplTest {

    @Mock private ScoreMapper mapper;
    @InjectMocks private ScoreServiceImpl service;

    private Score score;

    @BeforeEach
    void setUp() {
        score = new Score();
        score.setScoreId(1L);
    }

    @Test
    void insert_setsTimes() {
        when(mapper.insertScore(any())).thenReturn(1);
        int rows = service.insertScore(score);
        assertEquals(1, rows);
        assertNotNull(score.getCreateTime());
        assertNotNull(score.getUpdateTime());
        assertNotNull(score.getSubmitTime());
    }

    @Test
    void update_setsUpdateTime() {
        when(mapper.updateScore(any())).thenReturn(1);
        int rows = service.updateScore(score);
        assertEquals(1, rows);
        assertNotNull(score.getUpdateTime());
    }

    @Test
    void selectAndDelete_delegate() {
        when(mapper.selectScoreByScoreId(1L)).thenReturn(score);
        when(mapper.selectScoreList(any())).thenReturn(Collections.singletonList(score));
        when(mapper.deleteScoreByScoreIds(any())).thenReturn(2);
        when(mapper.deleteScoreByScoreId(1L)).thenReturn(1);
        when(mapper.selectScoreByUserAndCourse(2L,3L)).thenReturn(Collections.singletonList(score));
        when(mapper.selectScoreByUserId(2L)).thenReturn(Collections.singletonList(score));

        assertEquals(score, service.selectScoreByScoreId(1L));
        assertEquals(1, service.selectScoreList(new Score()).size());
        assertEquals(2, service.deleteScoreByScoreIds(new Long[]{1L,2L}));
        assertEquals(1, service.deleteScoreByScoreId(1L));
        assertEquals(1, service.selectScoreByUserAndCourse(2L,3L).size());
        assertEquals(1, service.selectScoreByUserId(2L).size());
    }

    @Test
    void insert_withSubmitTimeProvided_shouldNotOverride() {
        java.util.Date custom = new java.util.Date(123456789L);
        score.setSubmitTime(custom);
        when(mapper.insertScore(any())).thenReturn(1);
        int rows = service.insertScore(score);
        assertEquals(1, rows);
        assertEquals(custom, score.getSubmitTime());
    }

    @Test
    void selectById_mapperReturnsNull_shouldReturnNull() {
        when(mapper.selectScoreByScoreId(99L)).thenReturn(null);
        assertNull(service.selectScoreByScoreId(99L));
    }

    @Test
    void selectList_mapperReturnsNull_shouldThrowNPE() {
        when(mapper.selectScoreList(any())).thenReturn(null);
        // 由于日志打印 list.size()，此处会抛NPE
        assertThrows(NullPointerException.class, () -> service.selectScoreList(new Score()));
    }

    @Test
    void selectScoreByUserAndCourse_mapperReturnsNull_shouldReturnNull() {
        when(mapper.selectScoreByUserAndCourse(2L,3L)).thenReturn(null);
        assertNull(service.selectScoreByUserAndCourse(2L,3L));
    }

    @Test
    void selectScoreByUserId_mapperReturnsNull_shouldThrowNPE() {
        when(mapper.selectScoreByUserId(2L)).thenReturn(null);
        // 由于日志打印 list.size()，此处会抛NPE
        assertThrows(NullPointerException.class, () -> service.selectScoreByUserId(2L));
    }

    @Test
    void deleteByScoreIds_emptyArray_shouldReturnZero() {
        when(mapper.deleteScoreByScoreIds(new Long[0])).thenReturn(0);
        assertEquals(0, service.deleteScoreByScoreIds(new Long[0]));
    }

    @Test
    void allMapperMethods_throwException_shouldPropagate() {
        RuntimeException ex = new RuntimeException("db error");
        when(mapper.selectScoreByScoreId(anyLong())).thenThrow(ex);
        when(mapper.selectScoreList(any())).thenThrow(ex);
        when(mapper.insertScore(any())).thenThrow(ex);
        when(mapper.updateScore(any())).thenThrow(ex);
        when(mapper.deleteScoreByScoreIds(any())).thenThrow(ex);
        when(mapper.deleteScoreByScoreId(anyLong())).thenThrow(ex);
        when(mapper.selectScoreByUserAndCourse(anyLong(), anyLong())).thenThrow(ex);
        when(mapper.selectScoreByUserId(anyLong())).thenThrow(ex);
        assertThrows(RuntimeException.class, () -> service.selectScoreByScoreId(1L));
        assertThrows(RuntimeException.class, () -> service.selectScoreList(new Score()));
        assertThrows(RuntimeException.class, () -> service.insertScore(score));
        assertThrows(RuntimeException.class, () -> service.updateScore(score));
        assertThrows(RuntimeException.class, () -> service.deleteScoreByScoreIds(new Long[]{1L}));
        assertThrows(RuntimeException.class, () -> service.deleteScoreByScoreId(1L));
        assertThrows(RuntimeException.class, () -> service.selectScoreByUserAndCourse(1L,2L));
        assertThrows(RuntimeException.class, () -> service.selectScoreByUserId(1L));
    }
} 