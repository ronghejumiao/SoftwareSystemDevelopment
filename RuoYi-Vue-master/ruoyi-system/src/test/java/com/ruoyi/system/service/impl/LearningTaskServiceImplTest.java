package com.ruoyi.system.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ruoyi.system.domain.LearningTask;
import com.ruoyi.system.mapper.LearningTaskMapper;

class LearningTaskServiceImplTest {

    @Mock
    private LearningTaskMapper learningTaskMapper;

    @InjectMocks
    private LearningTaskServiceImpl learningTaskService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSelectLearningTaskByTaskId() {
        Long taskId = 1L;
        LearningTask expectedTask = new LearningTask();
        expectedTask.setTaskId(taskId);

        when(learningTaskMapper.selectLearningTaskByTaskId(taskId)).thenReturn(expectedTask);

        LearningTask result = learningTaskService.selectLearningTaskByTaskId(taskId);

        assertEquals(expectedTask, result);
        verify(learningTaskMapper).selectLearningTaskByTaskId(taskId);
    }

    @Test
    void testSelectLearningTaskList() {
        LearningTask query = new LearningTask();
        List<LearningTask> expectedList = Arrays.asList(new LearningTask(), new LearningTask());

        when(learningTaskMapper.selectLearningTaskList(query)).thenReturn(expectedList);

        List<LearningTask> result = learningTaskService.selectLearningTaskList(query);

        assertEquals(expectedList, result);
        verify(learningTaskMapper).selectLearningTaskList(query);
    }


    @Test
    void testInsertLearningTask() {
        LearningTask task = new LearningTask();
        task.setTaskName("Test Task");

        when(learningTaskMapper.insertLearningTask(task)).thenReturn(1);

        int result = learningTaskService.insertLearningTask(task);

        assertEquals(1, result);
        assertNotNull(task.getCreateTime());
        verify(learningTaskMapper).insertLearningTask(task);
    }

    @Test
    void testUpdateLearningTask() {
        LearningTask task = new LearningTask();
        task.setTaskId(1L);
        task.setTaskName("Updated Task");

        when(learningTaskMapper.updateLearningTask(task)).thenReturn(1);

        int result = learningTaskService.updateLearningTask(task);

        assertEquals(1, result);
        assertNotNull(task.getUpdateTime());
        verify(learningTaskMapper).updateLearningTask(task);
    }

    @Test
    void testDeleteLearningTaskByTaskIds() {
        Long[] taskIds = {1L, 2L};

        when(learningTaskMapper.deleteLearningTaskByTaskIds(taskIds)).thenReturn(2);

        int result = learningTaskService.deleteLearningTaskByTaskIds(taskIds);

        assertEquals(2, result);
        verify(learningTaskMapper).deleteLearningTaskByTaskIds(taskIds);
    }

    @Test
    void testSelectLearningTaskByTaskId_mapperReturnsNull() {
        when(learningTaskMapper.selectLearningTaskByTaskId(99L)).thenReturn(null);
        assertNull(learningTaskService.selectLearningTaskByTaskId(99L));
    }

    @Test
    void testSelectLearningTaskList_mapperReturnsNull() {
        when(learningTaskMapper.selectLearningTaskList(any())).thenReturn(null);
        assertNull(learningTaskService.selectLearningTaskList(new LearningTask()));
    }

    @Test
    void testInsertLearningTask_mapperThrowsException() {
        LearningTask task = new LearningTask();
        doThrow(new RuntimeException("db error")).when(learningTaskMapper).insertLearningTask(any());
        assertThrows(RuntimeException.class, () -> learningTaskService.insertLearningTask(task));
    }

    @Test
    void testUpdateLearningTask_mapperThrowsException() {
        LearningTask task = new LearningTask();
        doThrow(new RuntimeException("db error")).when(learningTaskMapper).updateLearningTask(any());
        assertThrows(RuntimeException.class, () -> learningTaskService.updateLearningTask(task));
    }

    @Test
    void testDeleteLearningTaskByTaskIds_emptyArray() {
        when(learningTaskMapper.deleteLearningTaskByTaskIds(new Long[0])).thenReturn(0);
        assertEquals(0, learningTaskService.deleteLearningTaskByTaskIds(new Long[0]));
    }

    @Test
    void testDeleteLearningTaskByTaskIds_mapperThrowsException() {
        doThrow(new RuntimeException("db error")).when(learningTaskMapper).deleteLearningTaskByTaskIds(any());
        assertThrows(RuntimeException.class, () -> learningTaskService.deleteLearningTaskByTaskIds(new Long[]{1L}));
    }

    @Test
    void testDeleteLearningTaskByTaskId_mapperThrowsException() {
        doThrow(new RuntimeException("db error")).when(learningTaskMapper).deleteLearningTaskByTaskId(anyLong());
        assertThrows(RuntimeException.class, () -> learningTaskService.deleteLearningTaskByTaskId(1L));
    }

    @Test
    void testSelectLearningTaskListByCourseIdAndSubmitMethod_mapperReturnsNull() {
        when(learningTaskMapper.selectLearningTaskList(any())).thenReturn(null);
        assertNull(learningTaskService.selectLearningTaskListByCourseIdAndSubmitMethod(1L, "online"));
    }
}