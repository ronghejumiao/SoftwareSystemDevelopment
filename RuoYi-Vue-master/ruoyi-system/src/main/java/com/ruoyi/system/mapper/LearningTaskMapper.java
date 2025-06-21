package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.LearningTask;

/**
 * 学习任务，存储课程的学习任务信息Mapper接口
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
public interface LearningTaskMapper 
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
     * 删除学习任务，存储课程的学习任务信息
     * 
     * @param taskId 学习任务，存储课程的学习任务信息主键
     * @return 结果
     */
    public int deleteLearningTaskByTaskId(Long taskId);

    /**
     * 批量删除学习任务，存储课程的学习任务信息
     * 
     * @param taskIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteLearningTaskByTaskIds(Long[] taskIds);
}
