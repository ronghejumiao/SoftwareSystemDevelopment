package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.LearningTaskMapper;
import com.ruoyi.system.domain.LearningTask;
import com.ruoyi.system.service.ILearningTaskService;

/**
 * 学习任务，存储课程的学习任务信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
@Service
public class LearningTaskServiceImpl implements ILearningTaskService 
{
    @Autowired
    private LearningTaskMapper learningTaskMapper;

    /**
     * 查询学习任务，存储课程的学习任务信息
     * 
     * @param taskId 学习任务，存储课程的学习任务信息主键
     * @return 学习任务，存储课程的学习任务信息
     */
    @Override
    public LearningTask selectLearningTaskByTaskId(Long taskId)
    {
        return learningTaskMapper.selectLearningTaskByTaskId(taskId);
    }

    /**
     * 查询学习任务，存储课程的学习任务信息列表
     * 
     * @param learningTask 学习任务，存储课程的学习任务信息
     * @return 学习任务，存储课程的学习任务信息
     */
    @Override
    public List<LearningTask> selectLearningTaskList(LearningTask learningTask)
    {
        return learningTaskMapper.selectLearningTaskList(learningTask);
    }

    /**
     * 新增学习任务，存储课程的学习任务信息
     * 
     * @param learningTask 学习任务，存储课程的学习任务信息
     * @return 结果
     */
    @Override
    public int insertLearningTask(LearningTask learningTask)
    {
        learningTask.setCreateTime(DateUtils.getNowDate());
        return learningTaskMapper.insertLearningTask(learningTask);
    }

    /**
     * 修改学习任务，存储课程的学习任务信息
     * 
     * @param learningTask 学习任务，存储课程的学习任务信息
     * @return 结果
     */
    @Override
    public int updateLearningTask(LearningTask learningTask)
    {
        learningTask.setUpdateTime(DateUtils.getNowDate());
        return learningTaskMapper.updateLearningTask(learningTask);
    }

    /**
     * 批量删除学习任务，存储课程的学习任务信息
     * 
     * @param taskIds 需要删除的学习任务，存储课程的学习任务信息主键
     * @return 结果
     */
    @Override
    public int deleteLearningTaskByTaskIds(Long[] taskIds)
    {
        return learningTaskMapper.deleteLearningTaskByTaskIds(taskIds);
    }

    /**
     * 删除学习任务，存储课程的学习任务信息信息
     * 
     * @param taskId 学习任务，存储课程的学习任务信息主键
     * @return 结果
     */
    @Override
    public int deleteLearningTaskByTaskId(Long taskId)
    {
        return learningTaskMapper.deleteLearningTaskByTaskId(taskId);
    }
}
