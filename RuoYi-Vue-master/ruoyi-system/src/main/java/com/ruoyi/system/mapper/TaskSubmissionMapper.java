package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.TaskSubmission;

/**
 * 任务提交记录Mapper接口
 * 
 * @author ruoyi
 * @date 2025-06-24
 */
public interface TaskSubmissionMapper 
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
     * 删除任务提交记录
     * 
     * @param submissionId 任务提交记录主键
     * @return 结果
     */
    public int deleteTaskSubmissionBySubmissionId(Long submissionId);

    /**
     * 批量删除任务提交记录
     * 
     * @param submissionIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTaskSubmissionBySubmissionIds(Long[] submissionIds);

    /**
     * 根据学习记录ID和任务ID查询提交，用于同步更新
     */
    TaskSubmission selectByRecordAndTaskId(@org.apache.ibatis.annotations.Param("recordId") Long recordId,
                                           @org.apache.ibatis.annotations.Param("taskId") Long taskId);
}
