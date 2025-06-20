package com.ruoyi.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 任务提交记录，记录学生提交任务的信息对象 task_submission
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
public class TaskSubmission extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 提交ID，主键，自增 */
    private Long submissionId;

    /** 学习记录ID，关联learning_record表 */
    @Excel(name = "学习记录ID，关联learning_record表")
    private Long recordId;

    /** 任务ID，关联learning_task表 */
    @Excel(name = "任务ID，关联learning_task表")
    private Long taskId;

    /** 提交用户ID，关联sys_user表 */
    @Excel(name = "提交用户ID，关联sys_user表")
    private Long userId;

    /** 提交内容 */
    @Excel(name = "提交内容")
    private String submissionContent;

    /** 提交文件路径 */
    @Excel(name = "提交文件路径")
    private String submissionFile;

    /** 提交时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "提交时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date submissionTime;

    /** 是否评分（0-未评分，1-已评分） */
    @Excel(name = "是否评分", readConverterExp = "0=-未评分，1-已评分")
    private String isGraded;

    /** 评分分数 */
    @Excel(name = "评分分数")
    private BigDecimal gradeScore;

    /** 评分评语 */
    @Excel(name = "评分评语")
    private String gradeComment;

    /** 评分人ID，关联sys_user表 */
    @Excel(name = "评分人ID，关联sys_user表")
    private Long graderId;

    /** 评分时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "评分时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date gradeTime;

    /** 状态（1-正常，0-已撤回） */
    @Excel(name = "状态", readConverterExp = "1=-正常，0-已撤回")
    private String status;

    /** 删除标志（0-正常，2-已删除） */
    private String delFlag;

    /** 租户ID */
    @Excel(name = "租户ID")
    private Long tenantId;

    public void setSubmissionId(Long submissionId) 
    {
        this.submissionId = submissionId;
    }

    public Long getSubmissionId() 
    {
        return submissionId;
    }

    public void setRecordId(Long recordId) 
    {
        this.recordId = recordId;
    }

    public Long getRecordId() 
    {
        return recordId;
    }

    public void setTaskId(Long taskId) 
    {
        this.taskId = taskId;
    }

    public Long getTaskId() 
    {
        return taskId;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setSubmissionContent(String submissionContent) 
    {
        this.submissionContent = submissionContent;
    }

    public String getSubmissionContent() 
    {
        return submissionContent;
    }

    public void setSubmissionFile(String submissionFile) 
    {
        this.submissionFile = submissionFile;
    }

    public String getSubmissionFile() 
    {
        return submissionFile;
    }

    public void setSubmissionTime(Date submissionTime) 
    {
        this.submissionTime = submissionTime;
    }

    public Date getSubmissionTime() 
    {
        return submissionTime;
    }

    public void setIsGraded(String isGraded) 
    {
        this.isGraded = isGraded;
    }

    public String getIsGraded() 
    {
        return isGraded;
    }

    public void setGradeScore(BigDecimal gradeScore) 
    {
        this.gradeScore = gradeScore;
    }

    public BigDecimal getGradeScore() 
    {
        return gradeScore;
    }

    public void setGradeComment(String gradeComment) 
    {
        this.gradeComment = gradeComment;
    }

    public String getGradeComment() 
    {
        return gradeComment;
    }

    public void setGraderId(Long graderId) 
    {
        this.graderId = graderId;
    }

    public Long getGraderId() 
    {
        return graderId;
    }

    public void setGradeTime(Date gradeTime) 
    {
        this.gradeTime = gradeTime;
    }

    public Date getGradeTime() 
    {
        return gradeTime;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    public void setTenantId(Long tenantId) 
    {
        this.tenantId = tenantId;
    }

    public Long getTenantId() 
    {
        return tenantId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("submissionId", getSubmissionId())
            .append("recordId", getRecordId())
            .append("taskId", getTaskId())
            .append("userId", getUserId())
            .append("submissionContent", getSubmissionContent())
            .append("submissionFile", getSubmissionFile())
            .append("submissionTime", getSubmissionTime())
            .append("isGraded", getIsGraded())
            .append("gradeScore", getGradeScore())
            .append("gradeComment", getGradeComment())
            .append("graderId", getGraderId())
            .append("gradeTime", getGradeTime())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("status", getStatus())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .append("tenantId", getTenantId())
            .toString();
    }
}
