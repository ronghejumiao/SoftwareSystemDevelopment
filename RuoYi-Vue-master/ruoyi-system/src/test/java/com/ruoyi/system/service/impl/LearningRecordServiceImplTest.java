package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.LearningRecord;
import com.ruoyi.system.mapper.LearningRecordMapper;
import com.ruoyi.system.mapper.ScoreMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * LearningRecordServiceImpl 单元测试
 */
@ExtendWith(MockitoExtension.class)
class LearningRecordServiceImplTest {

    @Mock
    private LearningRecordMapper mapper;
    @Mock
    private ScoreMapper scoreMapper;

    @InjectMocks
    private LearningRecordServiceImpl service;

    private LearningRecord record;

    @BeforeEach
    void setUp() {
        record = new LearningRecord();
        record.setUserId(2L);
        record.setCourseId(3L);
    }

    @Test
    void insert_whenNoExisting_shouldCallInsert() {
        when(mapper.selectLearningRecordByUserIdAndCourseId(2L,3L)).thenReturn(null);
        when(mapper.insertLearningRecord(any())).thenReturn(1);

        int res = service.insertLearningRecord(record);

        assertEquals(1, res);
        assertNotNull(record.getCreateTime());
        verify(mapper).insertLearningRecord(record);
    }

    @Test
    void insert_whenExisting_shouldUpdateInstead() {
        LearningRecord existing = new LearningRecord();
        existing.setRecordId(99L);
        when(mapper.selectLearningRecordByUserIdAndCourseId(2L,3L)).thenReturn(existing);
        when(mapper.updateLearningRecord(any())).thenReturn(1);

        int res = service.insertLearningRecord(record);

        assertEquals(1, res);
        assertEquals(99L, record.getRecordId());
        verify(mapper).updateLearningRecord(record);
    }

    @Test
    void insert_duplicateKeyException_shouldFallbackToUpdate() {
        // first select returns null -> try insert throws DuplicateKeyException, then select returns dup, then update
        LearningRecord dup = new LearningRecord();
        dup.setRecordId(77L);
        when(mapper.selectLearningRecordByUserIdAndCourseId(2L,3L)).thenReturn(null) // first call
                .thenReturn(dup); // second call after dup
        when(mapper.insertLearningRecord(any())).thenThrow(new DuplicateKeyException("dup"));
        when(mapper.updateLearningRecord(any())).thenReturn(1);

        int res = service.insertLearningRecord(record);
        assertEquals(1, res);
        assertEquals(77L, record.getRecordId());
        verify(mapper, times(1)).updateLearningRecord(record);
    }

    @Test
    void deleteLearningRecordByRecordIds_shouldDeleteScoresFirst() {
        when(mapper.deleteLearningRecordByRecordIds(any())).thenReturn(2);

        int res = service.deleteLearningRecordByRecordIds(new Long[]{1L,2L});
        assertEquals(2, res);
        verify(scoreMapper).deleteScoreByLearningRecordIds(any());
    }

    @Test
    void updateLearningRecord_setsUpdateTime() {
        when(mapper.updateLearningRecord(any())).thenReturn(1);
        int res = service.updateLearningRecord(record);
        assertEquals(1, res);
        assertNotNull(record.getUpdateTime());
        verify(mapper).updateLearningRecord(record);
    }

    @Test
    void selectMethods_delegate() {
        when(mapper.selectLearningRecordByRecordId(10L)).thenReturn(record);
        when(mapper.selectLearningRecordList(any())).thenReturn(java.util.Collections.singletonList(record));
        assertEquals(record, service.selectLearningRecordByRecordId(10L));
        assertEquals(1, service.selectLearningRecordList(new LearningRecord()).size());
        verify(mapper).selectLearningRecordByRecordId(10L);
        verify(mapper).selectLearningRecordList(any());
    }

    @Test
    void deleteLearningRecordByRecordId_delegate() {
        when(mapper.deleteLearningRecordByRecordId(5L)).thenReturn(1);
        assertEquals(1, service.deleteLearningRecordByRecordId(5L));
        verify(mapper).deleteLearningRecordByRecordId(5L);
    }

    @Test
    void insert_duplicateKey_exceptionAndNoDup_shouldPropagate() {
        when(mapper.selectLearningRecordByUserIdAndCourseId(2L,3L)).thenReturn(null);
        when(mapper.insertLearningRecord(any())).thenThrow(new DuplicateKeyException("dup"));
        when(mapper.selectLearningRecordByUserIdAndCourseId(2L,3L)).thenReturn(null); // second call returns null again
        assertThrows(DuplicateKeyException.class, () -> service.insertLearningRecord(record));
    }

    @Test
    void deleteLearningRecordByRecordIds_null_shouldSkipScoreDelete() {
        when(mapper.deleteLearningRecordByRecordIds(null)).thenReturn(0);
        int res = service.deleteLearningRecordByRecordIds(null);
        assertEquals(0, res);
        verify(scoreMapper, never()).deleteScoreByLearningRecordIds(any());
    }
} 