package com.ruoyi.system.service.impl;
import com.ruoyi.system.domain.TaskSubmission;
import com.ruoyi.system.mapper.TaskSubmissionMapper;
import com.ruoyi.system.service.impl.TaskSubmissionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskSubmissionServiceImplTest {

    @InjectMocks
    private TaskSubmissionServiceImpl service;

    @Mock
    private TaskSubmissionMapper mapper;

    private TaskSubmission submission;

    @BeforeEach
    void setUp() {
        submission = new TaskSubmission();
        submission.setSubmissionId(1L);
        submission.setRecordId(2L);
        submission.setTaskId(3L);
        submission.setScore(BigDecimal.valueOf(90.0));
        submission.setSubmissionTime(null);
        submission.setIsGraded(null);
    }

    @Test
    void testSelectTaskSubmissionBySubmissionId() {
        when(mapper.selectTaskSubmissionBySubmissionId(1L)).thenReturn(submission);
        TaskSubmission result = service.selectTaskSubmissionBySubmissionId(1L);
        assertEquals(submission, result);
        verify(mapper).selectTaskSubmissionBySubmissionId(1L);
    }

    @Test
    void testSelectTaskSubmissionList() {
        List<TaskSubmission> list = Collections.singletonList(submission);
        when(mapper.selectTaskSubmissionList(submission)).thenReturn(list);
        List<TaskSubmission> result = service.selectTaskSubmissionList(submission);
        assertEquals(list, result);
        verify(mapper).selectTaskSubmissionList(submission);
    }

    @Test
    void testInsertTaskSubmission_AlreadyExists() {
        // 已存在相同(recordId, taskId)的记录，走更新分支
        TaskSubmission existing = new TaskSubmission();
        existing.setSubmissionId(10L);
        when(mapper.selectByRecordAndTaskId(2L, 3L)).thenReturn(existing);
        when(mapper.updateTaskSubmission(any())).thenReturn(1);

        int result = service.insertTaskSubmission(submission);

        assertEquals(1, result);
        assertEquals(10L, submission.getSubmissionId());
        verify(mapper).selectByRecordAndTaskId(2L, 3L);
        verify(mapper).updateTaskSubmission(submission);
        verify(mapper, never()).insertTaskSubmission(any());
    }

    @Test
    void testInsertTaskSubmission_NewRecord() {
        // 不存在相同(recordId, taskId)的记录，走插入分支
        when(mapper.selectByRecordAndTaskId(2L, 3L)).thenReturn(null);
        when(mapper.insertTaskSubmission(any())).thenReturn(1);

        int result = service.insertTaskSubmission(submission);

        assertEquals(1, result);
        assertEquals(submission.getScore(), submission.getGradeScore());
        assertNotNull(submission.getSubmissionTime());
        assertNotNull(submission.getCreateTime());
        assertEquals("0", submission.getIsGraded());
        verify(mapper).insertTaskSubmission(submission);
    }

    @Test
    void testInsertTaskSubmission_NewRecordWithNullScore() {
        submission.setScore(null);
        when(mapper.selectByRecordAndTaskId(2L, 3L)).thenReturn(null);
        when(mapper.insertTaskSubmission(any())).thenReturn(1);

        int result = service.insertTaskSubmission(submission);

        assertEquals(1, result);
        assertNull(submission.getGradeScore());
        assertNotNull(submission.getSubmissionTime());
        assertNotNull(submission.getCreateTime());
        assertEquals("0", submission.getIsGraded());
        verify(mapper).insertTaskSubmission(submission);
    }

    @Test
    void testUpdateTaskSubmission_WithScore() {
        when(mapper.updateTaskSubmission(submission)).thenReturn(1);
        int result = service.updateTaskSubmission(submission);
        assertEquals(1, result);
        assertEquals(submission.getScore(), submission.getGradeScore());
        verify(mapper).updateTaskSubmission(submission);
    }

    @Test
    void testUpdateTaskSubmission_WithoutScore() {
        submission.setScore(null);
        when(mapper.updateTaskSubmission(submission)).thenReturn(1);
        int result = service.updateTaskSubmission(submission);
        assertEquals(1, result);
        assertNull(submission.getGradeScore());
        verify(mapper).updateTaskSubmission(submission);
    }

    @Test
    void testDeleteTaskSubmissionBySubmissionIds() {
        Long[] ids = {1L, 2L};
        when(mapper.deleteTaskSubmissionBySubmissionIds(ids)).thenReturn(2);
        int result = service.deleteTaskSubmissionBySubmissionIds(ids);
        assertEquals(2, result);
        verify(mapper).deleteTaskSubmissionBySubmissionIds(ids);
    }

    @Test
    void testDeleteTaskSubmissionBySubmissionId() {
        when(mapper.deleteTaskSubmissionBySubmissionId(1L)).thenReturn(1);
        int result = service.deleteTaskSubmissionBySubmissionId(1L);
        assertEquals(1, result);
        verify(mapper).deleteTaskSubmissionBySubmissionId(1L);
    }

    @Test
    void testSelectByRecordId() {
        List<TaskSubmission> list = Arrays.asList(submission);
        TaskSubmission query = new TaskSubmission();
        query.setRecordId(2L);
        when(mapper.selectTaskSubmissionList(argThat(arg -> arg.getRecordId().equals(2L)))).thenReturn(list);

        List<TaskSubmission> result = service.selectByRecordId(2L);

        assertEquals(list, result);
        verify(mapper).selectTaskSubmissionList(argThat(arg -> arg.getRecordId().equals(2L)));
    }

    @Test
    void selectTaskSubmissionBySubmissionId_mapperReturnsNull_shouldReturnNull() {
        when(mapper.selectTaskSubmissionBySubmissionId(99L)).thenReturn(null);
        assertNull(service.selectTaskSubmissionBySubmissionId(99L));
    }

    @Test
    void selectTaskSubmissionList_mapperReturnsNull_shouldReturnNull() {
        when(mapper.selectTaskSubmissionList(any())).thenReturn(null);
        assertNull(service.selectTaskSubmissionList(new TaskSubmission()));
    }

    @Test
    void insertTaskSubmission_updateException_shouldPropagate() {
        TaskSubmission existing = new TaskSubmission();
        existing.setSubmissionId(10L);
        when(mapper.selectByRecordAndTaskId(2L, 3L)).thenReturn(existing);
        when(mapper.updateTaskSubmission(any())).thenThrow(new RuntimeException("db error"));
        assertThrows(RuntimeException.class, () -> service.insertTaskSubmission(submission));
    }

    @Test
    void insertTaskSubmission_insertException_shouldPropagate() {
        when(mapper.selectByRecordAndTaskId(2L, 3L)).thenReturn(null);
        when(mapper.insertTaskSubmission(any())).thenThrow(new RuntimeException("db error"));
        assertThrows(RuntimeException.class, () -> service.insertTaskSubmission(submission));
    }

    @Test
    void updateTaskSubmission_updateException_shouldPropagate() {
        when(mapper.updateTaskSubmission(any())).thenThrow(new RuntimeException("db error"));
        assertThrows(RuntimeException.class, () -> service.updateTaskSubmission(submission));
    }

    @Test
    void deleteTaskSubmissionBySubmissionIds_emptyArray_shouldReturnZero() {
        when(mapper.deleteTaskSubmissionBySubmissionIds(new Long[0])).thenReturn(0);
        assertEquals(0, service.deleteTaskSubmissionBySubmissionIds(new Long[0]));
    }

}