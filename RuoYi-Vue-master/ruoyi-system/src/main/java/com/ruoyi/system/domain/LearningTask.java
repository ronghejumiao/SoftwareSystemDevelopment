package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 学习任务，存储课程的学习任务信息对象 learning_task
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
public class LearningTask extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 任务ID，主键，自增 */
    private Long taskId;

    /** 课程ID，关联course表 */
    @Excel(name = "课程ID，关联course表")
    private Long courseId;

    /** 任务名称 */
    @Excel(name = "任务名称")
    private String taskName;

    /** 任务类型（资料阅读、作业完成） */
    @Excel(name = "任务类型", readConverterExp = "资料阅读=资料阅读、作业完成=作业完成")
    private String taskType;

    /** 任务描述 */
    @Excel(name = "任务描述")
    private String taskDesc;

    /** 截止时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "截止时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date dueDate;

    /** 提交方式（资料阅读、作业完成） */
    @Excel(name = "提交方式", readConverterExp = "资料阅读=资料阅读、作业完成=作业完成")
    private String submitMethod;

    /** 资源ID，关联learning_resource表 */
    @Excel(name = "资源ID")
    private Long resourceId;

    /** 作业ID，关联course_homework表 */
    @Excel(name = "作业ID")
    private Long homeworkId;

    /** 状态（1-启用，0-停用） */
    @Excel(name = "状态", readConverterExp = "1=-启用，0-停用")
    private String status;

    public void setTaskId(Long taskId) 
    {
        this.taskId = taskId;
    }

    public Long getTaskId() 
    {
        return taskId;
    }

    public void setCourseId(Long courseId) 
    {
        this.courseId = courseId;
    }

    public Long getCourseId() 
    {
        return courseId;
    }

    public void setTaskName(String taskName) 
    {
        this.taskName = taskName;
    }

    public String getTaskName() 
    {
        return taskName;
    }

    public void setTaskType(String taskType) 
    {
        this.taskType = taskType;
    }

    public String getTaskType() 
    {
        return taskType;
    }

    public void setTaskDesc(String taskDesc) 
    {
        this.taskDesc = taskDesc;
    }

    public String getTaskDesc() 
    {
        return taskDesc;
    }

    public void setDueDate(Date dueDate) 
    {
        this.dueDate = dueDate;
    }

    public Date getDueDate() 
    {
        return dueDate;
    }

    public void setSubmitMethod(String submitMethod) 
    {
        this.submitMethod = submitMethod;
    }

    public String getSubmitMethod() 
    {
        return submitMethod;
    }

    public void setResourceId(Long resourceId) 
    {
        this.resourceId = resourceId;
    }

    public Long getResourceId() 
    {
        return resourceId;
    }

    public void setHomeworkId(Long homeworkId) 
    {
        this.homeworkId = homeworkId;
    }

    public Long getHomeworkId() 
    {
        return homeworkId;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("taskId", getTaskId())
            .append("courseId", getCourseId())
            .append("taskName", getTaskName())
            .append("taskType", getTaskType())
            .append("taskDesc", getTaskDesc())
            .append("dueDate", getDueDate())
            .append("submitMethod", getSubmitMethod())
            .append("resourceId", getResourceId())
            .append("homeworkId", getHomeworkId())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("status", getStatus())
            .append("remark", getRemark())
            .toString();
    }
}
