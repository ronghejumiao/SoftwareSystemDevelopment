package com.ruoyi.system.service.impl;

import java.util.List;
import java.util.ArrayList;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.TaskSubmissionMapper;
import com.ruoyi.system.domain.TaskSubmission;
import com.ruoyi.system.service.ITaskSubmissionService;
import com.ruoyi.system.service.IStudentSkillAssessmentService;
import com.ruoyi.system.service.ILearningRecordService;
import com.ruoyi.system.domain.LearningRecord;

/**
 * 任务提交记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-06-24
 */
@Service
public class TaskSubmissionServiceImpl implements ITaskSubmissionService 
{
    @Autowired
    private TaskSubmissionMapper taskSubmissionMapper;
    
    @Autowired
    private IStudentSkillAssessmentService studentSkillAssessmentService;
    
    @Autowired
    private ILearningRecordService learningRecordService;

    /**
     * 查询任务提交记录
     * 
     * @param submissionId 任务提交记录主键
     * @return 任务提交记录
     */
    @Override
    public TaskSubmission selectTaskSubmissionBySubmissionId(Long submissionId)
    {
        return taskSubmissionMapper.selectTaskSubmissionBySubmissionId(submissionId);
    }

    /**
     * 查询任务提交记录列表
     * 
     * @param taskSubmission 任务提交记录
     * @return 任务提交记录
     */
    @Override
    public List<TaskSubmission> selectTaskSubmissionList(TaskSubmission taskSubmission)
    {
        return taskSubmissionMapper.selectTaskSubmissionList(taskSubmission);
    }

    /**
     * 新增任务提交记录
     * 
     * @param taskSubmission 任务提交记录
     * @return 结果
     */
    @Override
    public int insertTaskSubmission(TaskSubmission taskSubmission)
    {
        // 检查是否已存在相同(recordId, taskId)的记录，如果存在则执行更新操作
        TaskSubmission existing = taskSubmissionMapper.selectByRecordAndTaskId(
            taskSubmission.getRecordId(), taskSubmission.getTaskId());
        
        if (existing != null) {
            // 设置提交ID，进行更新操作
            taskSubmission.setSubmissionId(existing.getSubmissionId());
            return updateTaskSubmission(taskSubmission);
        }
        
        // 设置默认值
        if (taskSubmission.getScore() != null) {
            taskSubmission.setGradeScore(taskSubmission.getScore());
        }
        if (taskSubmission.getSubmissionTime() == null) {
            taskSubmission.setSubmissionTime(new java.util.Date());
        }
        // 确保 submissionTime 与 createTime 保持一致
        taskSubmission.setCreateTime(taskSubmission.getSubmissionTime());
        if (taskSubmission.getIsGraded() == null) {
            taskSubmission.setIsGraded("0"); // 默认未评分
        }
        
        int result = taskSubmissionMapper.insertTaskSubmission(taskSubmission);
        
        // 触发能力评估
        if (result > 0) {
            triggerAssessment(taskSubmission.getRecordId());
        }
        
        return result;
    }

    /**
     * 修改任务提交记录
     * 
     * @param taskSubmission 任务提交记录
     * @return 结果
     */
    @Override
    public int updateTaskSubmission(TaskSubmission taskSubmission)
    {
        if (taskSubmission.getScore() != null) {
            taskSubmission.setGradeScore(taskSubmission.getScore());
        }
        
        int result = taskSubmissionMapper.updateTaskSubmission(taskSubmission);
        
        // 触发能力评估（特别是评分变更时）
        if (result > 0) {
            triggerAssessment(taskSubmission.getRecordId());
        }
        
        return result;
    }

    /**
     * 批量删除任务提交记录
     * 
     * @param submissionIds 需要删除的任务提交记录主键
     * @return 结果
     */
    @Override
    public int deleteTaskSubmissionBySubmissionIds(Long[] submissionIds)
    {
        // 获取要删除的记录信息，用于触发评估
        List<TaskSubmission> submissions = taskSubmissionMapper.selectTaskSubmissionBySubmissionIds(submissionIds);
        
        int result = taskSubmissionMapper.deleteTaskSubmissionBySubmissionIds(submissionIds);
        
        // 触发能力评估
        if (result > 0 && submissions != null) {
            for (TaskSubmission submission : submissions) {
                triggerAssessment(submission.getRecordId());
            }
        }
        
        return result;
    }

    /**
     * 删除任务提交记录信息
     * 
     * @param submissionId 任务提交记录主键
     * @return 结果
     */
    @Override
    public int deleteTaskSubmissionBySubmissionId(Long submissionId)
    {
        // 获取要删除的记录信息，用于触发评估
        TaskSubmission submission = taskSubmissionMapper.selectTaskSubmissionBySubmissionId(submissionId);
        
        int result = taskSubmissionMapper.deleteTaskSubmissionBySubmissionId(submissionId);
        
        // 触发能力评估
        if (result > 0 && submission != null) {
            triggerAssessment(submission.getRecordId());
        }
        
        return result;
    }

    @Override
    public java.util.List<com.ruoyi.system.domain.TaskSubmission> selectByRecordId(Long recordId) {
        com.ruoyi.system.domain.TaskSubmission query = new com.ruoyi.system.domain.TaskSubmission();
        query.setRecordId(recordId);
        return taskSubmissionMapper.selectTaskSubmissionList(query);
    }
    
    @Override
    public List<TaskSubmission> selectTaskSubmissionListByLearningRecords(List<Long> recordIds) {
        if (recordIds == null || recordIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<TaskSubmission> result = new ArrayList<>();
        for (Long recordId : recordIds) {
            List<TaskSubmission> submissions = selectByRecordId(recordId);
            result.addAll(submissions);
        }
        return result;
    }
    
    /**
     * 触发能力评估
     * @param recordId 学习记录ID
     */
    private void triggerAssessment(Long recordId) {
        try {
            LearningRecord record = learningRecordService.selectLearningRecordByRecordId(recordId);
            if (record != null) {
                studentSkillAssessmentService.triggerAssessmentOnDataChange(record.getUserId(), record.getCourseId());
            }
        } catch (Exception e) {
            // 记录日志但不影响主业务流程
            System.err.println("触发能力评估失败: recordId=" + recordId + ", error=" + e.getMessage());
        }
    }
}
