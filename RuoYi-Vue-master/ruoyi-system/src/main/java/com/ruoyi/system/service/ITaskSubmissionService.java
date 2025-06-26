package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.TaskSubmission;

/**
 * 任务提交记录Service接口
 * 
 * @author ruoyi
 * @date 2025-06-24
 */
public interface ITaskSubmissionService 
{
    /**
     * 查询任务提交记录
     * 
     * @param submissionId 任务提交记录主键
     * @return 任务提交记录
     */
    public TaskSubmission selectTaskSubmissionBySubmissionId(Long submissionId);

    /**
     * 查询任务提交记录列表
     * 
     * @param taskSubmission 任务提交记录
     * @return 任务提交记录集合
     */
    public List<TaskSubmission> selectTaskSubmissionList(TaskSubmission taskSubmission);

    /**
     * 新增任务提交记录
     * 
     * @param taskSubmission 任务提交记录
     * @return 结果
     */
    public int insertTaskSubmission(TaskSubmission taskSubmission);

    /**
     * 修改任务提交记录
     * 
     * @param taskSubmission 任务提交记录
     * @return 结果
     */
    public int updateTaskSubmission(TaskSubmission taskSubmission);

    /**
     * 批量删除任务提交记录
     * 
     * @param submissionIds 需要删除的任务提交记录主键集合
     * @return 结果
     */
    public int deleteTaskSubmissionBySubmissionIds(Long[] submissionIds);

    /**
     * 删除任务提交记录信息
     * 
     * @param submissionId 任务提交记录主键
     * @return 结果
     */
    public int deleteTaskSubmissionBySubmissionId(Long submissionId);

    /**
     * 根据学习记录ID查询任务提交列表
     * @param recordId 学习记录ID
     * @return 任务提交集合
     */
    public java.util.List<com.ruoyi.system.domain.TaskSubmission> selectByRecordId(Long recordId);
}
