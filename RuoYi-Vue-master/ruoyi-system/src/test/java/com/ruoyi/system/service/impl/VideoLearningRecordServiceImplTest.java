package com.ruoyi.system.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.VideoLearningRecord;
import com.ruoyi.system.mapper.VideoLearningRecordMapper;

class VideoLearningRecordServiceImplTest {

    @Mock
    private VideoLearningRecordMapper videoLearningRecordMapper;

    @InjectMocks
    private VideoLearningRecordServiceImpl videoLearningRecordService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSelectVideoLearningRecordByRecordId() {
        Long recordId = 1L;
        VideoLearningRecord expectedRecord = new VideoLearningRecord();
        expectedRecord.setRecordId(recordId);

        when(videoLearningRecordMapper.selectVideoLearningRecordByRecordId(recordId)).thenReturn(expectedRecord);

        VideoLearningRecord result = videoLearningRecordService.selectVideoLearningRecordByRecordId(recordId);

        assertEquals(expectedRecord, result);
        verify(videoLearningRecordMapper).selectVideoLearningRecordByRecordId(recordId);
    }

    @Test
    void testSelectVideoLearningRecordList() {
        VideoLearningRecord query = new VideoLearningRecord();
        List<VideoLearningRecord> expectedList = Arrays.asList(new VideoLearningRecord(), new VideoLearningRecord());

        when(videoLearningRecordMapper.selectVideoLearningRecordList(query)).thenReturn(expectedList);

        List<VideoLearningRecord> result = videoLearningRecordService.selectVideoLearningRecordList(query);

        assertEquals(expectedList, result);
        verify(videoLearningRecordMapper).selectVideoLearningRecordList(query);
    }

    @Test
    void testInsertVideoLearningRecord() {
        VideoLearningRecord record = new VideoLearningRecord();
        record.setResourceId(1L);

        when(videoLearningRecordMapper.insertVideoLearningRecord(record)).thenReturn(1);

        int result = videoLearningRecordService.insertVideoLearningRecord(record);

        assertEquals(1, result);
        assertNotNull(record.getCreateTime());
        assertNotNull(record.getLastWatchTime());
        verify(videoLearningRecordMapper).insertVideoLearningRecord(record);
    }

    @Test
    void testUpdateVideoLearningRecord() {
        VideoLearningRecord record = new VideoLearningRecord();
        record.setRecordId(1L);

        when(videoLearningRecordMapper.updateVideoLearningRecord(record)).thenReturn(1);

        int result = videoLearningRecordService.updateVideoLearningRecord(record);

        assertEquals(1, result);
        assertNotNull(record.getUpdateTime());
        verify(videoLearningRecordMapper).updateVideoLearningRecord(record);
    }

    @Test
    void testSaveOrUpdateNewRecord() {
        VideoLearningRecord record = new VideoLearningRecord();
        record.setLearningRecordId(1L);
        record.setResourceId(1L);

        when(videoLearningRecordMapper.selectByLearningRecordIdAndResourceId(record.getLearningRecordId(), record.getResourceId()))
                .thenReturn(null);
        when(videoLearningRecordMapper.insertVideoLearningRecord(record)).thenReturn(1);

        int result = videoLearningRecordService.saveOrUpdate(record);

        assertEquals(1, result);
        assertNotNull(record.getCreateTime());
        assertNotNull(record.getUpdateTime());
        verify(videoLearningRecordMapper).insertVideoLearningRecord(record);
    }

    @Test
    void testSaveOrUpdateExistingRecord() {
        VideoLearningRecord record = new VideoLearningRecord();
        record.setLearningRecordId(1L);
        record.setResourceId(1L);
        record.setWatchedDuration(100L);

        VideoLearningRecord existing = new VideoLearningRecord();
        existing.setRecordId(1L);
        existing.setWatchedDuration(50L);
        existing.setSkipSegments("1,2");
        existing.setRepeatSegments("3,4");

        when(videoLearningRecordMapper.selectByLearningRecordIdAndResourceId(record.getLearningRecordId(), record.getResourceId()))
                .thenReturn(existing);
        when(videoLearningRecordMapper.updateVideoLearningRecord(any(VideoLearningRecord.class))).thenReturn(1);

        int result = videoLearningRecordService.saveOrUpdate(record);

        assertEquals(1, result);
        assertEquals(1L, record.getRecordId());
        verify(videoLearningRecordMapper).updateVideoLearningRecord(any(VideoLearningRecord.class));
    }

    @Test
    void selectVideoLearningRecordByRecordId_mapperReturnsNull_shouldReturnNull() {
        when(videoLearningRecordMapper.selectVideoLearningRecordByRecordId(99L)).thenReturn(null);
        assertNull(videoLearningRecordService.selectVideoLearningRecordByRecordId(99L));
    }

    @Test
    void selectVideoLearningRecordList_mapperReturnsNull_shouldReturnNull() {
        when(videoLearningRecordMapper.selectVideoLearningRecordList(any())).thenReturn(null);
        assertNull(videoLearningRecordService.selectVideoLearningRecordList(new VideoLearningRecord()));
    }

    @Test
    void deleteVideoLearningRecordByRecordIds_emptyArray_shouldReturnZero() {
        when(videoLearningRecordMapper.deleteVideoLearningRecordByRecordIds(new Long[0])).thenReturn(0);
        assertEquals(0, videoLearningRecordService.deleteVideoLearningRecordByRecordIds(new Long[0]));
    }

    @Test
    void allMapperMethods_throwException_shouldPropagate() {
        RuntimeException ex = new RuntimeException("db error");
        when(videoLearningRecordMapper.selectVideoLearningRecordByRecordId(anyLong())).thenThrow(ex);
        when(videoLearningRecordMapper.selectVideoLearningRecordList(any())).thenThrow(ex);
        when(videoLearningRecordMapper.insertVideoLearningRecord(any())).thenThrow(ex);
        when(videoLearningRecordMapper.updateVideoLearningRecord(any())).thenThrow(ex);
        when(videoLearningRecordMapper.deleteVideoLearningRecordByRecordIds(any())).thenThrow(ex);
        when(videoLearningRecordMapper.deleteVideoLearningRecordByRecordId(anyLong())).thenThrow(ex);
        when(videoLearningRecordMapper.selectByLearningRecordIdAndResourceId(anyLong(), anyLong())).thenThrow(ex);
        assertThrows(RuntimeException.class, () -> videoLearningRecordService.selectVideoLearningRecordByRecordId(1L));
        assertThrows(RuntimeException.class, () -> videoLearningRecordService.selectVideoLearningRecordList(new VideoLearningRecord()));
        assertThrows(RuntimeException.class, () -> videoLearningRecordService.insertVideoLearningRecord(new VideoLearningRecord()));
        assertThrows(RuntimeException.class, () -> videoLearningRecordService.updateVideoLearningRecord(new VideoLearningRecord()));
        assertThrows(RuntimeException.class, () -> videoLearningRecordService.deleteVideoLearningRecordByRecordIds(new Long[]{1L}));
        assertThrows(RuntimeException.class, () -> videoLearningRecordService.deleteVideoLearningRecordByRecordId(1L));
        assertThrows(RuntimeException.class, () -> videoLearningRecordService.saveOrUpdate(new VideoLearningRecord()));
    }

    @Test
    void testSaveOrUpdate_mergeSegments() {
        VideoLearningRecord record = new VideoLearningRecord();
        record.setLearningRecordId(1L);
        record.setResourceId(1L);
        record.setWatchedDuration(0L);
        record.setSkipSegments("2,3");
        record.setRepeatSegments("4,5");

        VideoLearningRecord existing = new VideoLearningRecord();
        existing.setRecordId(1L);
        existing.setWatchedDuration(10L);
        existing.setCompletionRate(java.math.BigDecimal.valueOf(0.5));
        existing.setSkipSegments("1,2");
        existing.setRepeatSegments("3,4");

        when(videoLearningRecordMapper.selectByLearningRecordIdAndResourceId(1L, 1L)).thenReturn(existing);
        when(videoLearningRecordMapper.updateVideoLearningRecord(any())).thenReturn(1);

        int result = videoLearningRecordService.saveOrUpdate(record);
        assertEquals(1, result);
        assertEquals("1,2,3", record.getSkipSegments());
        assertEquals("3,4,5", record.getRepeatSegments());
        assertEquals(1L, record.getRecordId());
        assertEquals(10L, record.getWatchedDuration());
        assertEquals(java.math.BigDecimal.valueOf(0.5), record.getCompletionRate());
    }
}