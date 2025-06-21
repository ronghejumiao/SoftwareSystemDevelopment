package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.LearningTask;

/**
 * 学习任务，存储课程的学习任务信息Service接口
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
public interface ILearningTaskService 
{
    /**
     * 查询学习任务，存储课程的学习任务信息
     * 
     * @param taskId 学习任务，存储课程的学习任务信息主键
     * @return 学习任务，存储课程的学习任务信息
     */
    public LearningTask selectLearningTaskByTaskId(Long taskId);

    /**
     * 查询学习任务，存储课程的学习任务信息列表
     * 
     * @param learningTask 学习任务，存储课程的学习任务信息
     * @return 学习任务，存储课程的学习任务信息集合
     */
    public List<LearningTask> selectLearningTaskList(LearningTask learningTask);

    /**
     * 新增学习任务，存储课程的学习任务信息
     * 
     * @param learningTask 学习任务，存储课程的学习任务信息
     * @return 结果
     */
    public int insertLearningTask(LearningTask learningTask);

    /**
     * 修改学习任务，存储课程的学习任务信息
     * 
     * @param learningTask 学习任务，存储课程的学习任务信息
     * @return 结果
     */
    public int updateLearningTask(LearningTask learningTask);

    /**
     * 批量删除学习任务，存储课程的学习任务信息
     * 
     * @param taskIds 需要删除的学习任务，存储课程的学习任务信息主键集合
     * @return 结果
     */
    public int deleteLearningTaskByTaskIds(Long[] taskIds);

    /**
     * 删除学习任务，存储课程的学习任务信息信息
     * 
     * @param taskId 学习任务，存储课程的学习任务信息主键
     * @return 结果
     */
    public int deleteLearningTaskByTaskId(Long taskId);
}
