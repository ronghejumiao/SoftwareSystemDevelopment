package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.TaskSubmission;

/**
 * 任务提交记录，记录学生提交任务的信息Mapper接口
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
public interface TaskSubmissionMapper 
{
    /**
     * 查询任务提交记录，记录学生提交任务的信息
     * 
     * @param submissionId 任务提交记录，记录学生提交任务的信息主键
     * @return 任务提交记录，记录学生提交任务的信息
     */
    public TaskSubmission selectTaskSubmissionBySubmissionId(Long submissionId);

    /**
     * 查询任务提交记录，记录学生提交任务的信息列表
     * 
     * @param taskSubmission 任务提交记录，记录学生提交任务的信息
     * @return 任务提交记录，记录学生提交任务的信息集合
     */
    public List<TaskSubmission> selectTaskSubmissionList(TaskSubmission taskSubmission);

    /**
     * 新增任务提交记录，记录学生提交任务的信息
     * 
     * @param taskSubmission 任务提交记录，记录学生提交任务的信息
     * @return 结果
     */
    public int insertTaskSubmission(TaskSubmission taskSubmission);

    /**
     * 修改任务提交记录，记录学生提交任务的信息
     * 
     * @param taskSubmission 任务提交记录，记录学生提交任务的信息
     * @return 结果
     */
    public int updateTaskSubmission(TaskSubmission taskSubmission);

    /**
     * 删除任务提交记录，记录学生提交任务的信息
     * 
     * @param submissionId 任务提交记录，记录学生提交任务的信息主键
     * @return 结果
     */
    public int deleteTaskSubmissionBySubmissionId(Long submissionId);

    /**
     * 批量删除任务提交记录，记录学生提交任务的信息
     * 
     * @param submissionIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTaskSubmissionBySubmissionIds(Long[] submissionIds);
}
