package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.TaskSubmissionMapper;
import com.ruoyi.system.domain.TaskSubmission;
import com.ruoyi.system.service.ITaskSubmissionService;

/**
 * 任务提交记录，记录学生提交任务的信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
@Service
public class TaskSubmissionServiceImpl implements ITaskSubmissionService 
{
    @Autowired
    private TaskSubmissionMapper taskSubmissionMapper;

    /**
     * 查询任务提交记录，记录学生提交任务的信息
     * 
     * @param submissionId 任务提交记录，记录学生提交任务的信息主键
     * @return 任务提交记录，记录学生提交任务的信息
     */
    @Override
    public TaskSubmission selectTaskSubmissionBySubmissionId(Long submissionId)
    {
        return taskSubmissionMapper.selectTaskSubmissionBySubmissionId(submissionId);
    }

    /**
     * 查询任务提交记录，记录学生提交任务的信息列表
     * 
     * @param taskSubmission 任务提交记录，记录学生提交任务的信息
     * @return 任务提交记录，记录学生提交任务的信息
     */
    @Override
    public List<TaskSubmission> selectTaskSubmissionList(TaskSubmission taskSubmission)
    {
        return taskSubmissionMapper.selectTaskSubmissionList(taskSubmission);
    }

    /**
     * 新增任务提交记录，记录学生提交任务的信息
     * 
     * @param taskSubmission 任务提交记录，记录学生提交任务的信息
     * @return 结果
     */
    @Override
    public int insertTaskSubmission(TaskSubmission taskSubmission)
    {
        taskSubmission.setCreateTime(DateUtils.getNowDate());
        return taskSubmissionMapper.insertTaskSubmission(taskSubmission);
    }

    /**
     * 修改任务提交记录，记录学生提交任务的信息
     * 
     * @param taskSubmission 任务提交记录，记录学生提交任务的信息
     * @return 结果
     */
    @Override
    public int updateTaskSubmission(TaskSubmission taskSubmission)
    {
        taskSubmission.setUpdateTime(DateUtils.getNowDate());
        return taskSubmissionMapper.updateTaskSubmission(taskSubmission);
    }

    /**
     * 批量删除任务提交记录，记录学生提交任务的信息
     * 
     * @param submissionIds 需要删除的任务提交记录，记录学生提交任务的信息主键
     * @return 结果
     */
    @Override
    public int deleteTaskSubmissionBySubmissionIds(Long[] submissionIds)
    {
        return taskSubmissionMapper.deleteTaskSubmissionBySubmissionIds(submissionIds);
    }

    /**
     * 删除任务提交记录，记录学生提交任务的信息信息
     * 
     * @param submissionId 任务提交记录，记录学生提交任务的信息主键
     * @return 结果
     */
    @Override
    public int deleteTaskSubmissionBySubmissionId(Long submissionId)
    {
        return taskSubmissionMapper.deleteTaskSubmissionBySubmissionId(submissionId);
    }
}
