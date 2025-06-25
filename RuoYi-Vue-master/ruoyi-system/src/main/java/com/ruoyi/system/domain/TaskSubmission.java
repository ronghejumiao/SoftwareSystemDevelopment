package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 任务提交记录对象 task_submission
 * 
 * @author ruoyi
 * @date 2025-06-24
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

    /** 提交内容 */
    @Excel(name = "提交内容")
    private String submissionContent;

    /** 提交文件路径 */
    @Excel(name = "提交文件路径")
    private String submissionFile;

    /** 提交时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "提交时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date submissionTime;

    /** 是否评分（0-未评分，1-已评分） */
    @Excel(name = "是否评分", readConverterExp = "0=-未评分，1-已评分")
    private String isGraded;

    /** 评分评语 */
    @Excel(name = "评分评语")
    private String gradeComment;

    /** 用户ID */
    private Long userId;

    /** 评分得分 */
    private java.math.BigDecimal gradeScore;

    /** 评分人ID */
    private Long graderId;

    /** 评分时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.util.Date gradeTime;

    /** 状态 */
    private String status;

    /** 备注 */
    private String remark;

    /** 删除标识 */
    private String delFlag;

    /** 租户ID */
    private Long tenantId;

    /** 学生姓名 */
    private String studentName;

    /** 学生头像 */
    private String studentAvatar;

    /** 任务名称 */
    private String taskName;

    /** 课程名称 */
    private String courseName;

    /** 截止时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dueDate;

    /** 分数（用于前端展示，取自 gradeScore 别名） */
    private java.math.BigDecimal score;

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

    public void setGradeComment(String gradeComment) 
    {
        this.gradeComment = gradeComment;
    }

    public String getGradeComment() 
    {
        return gradeComment;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public java.math.BigDecimal getGradeScore() { return gradeScore; }
    public void setGradeScore(java.math.BigDecimal gradeScore) { this.gradeScore = gradeScore; }

    public Long getGraderId() { return graderId; }
    public void setGraderId(Long graderId) { this.graderId = graderId; }

    public java.util.Date getGradeTime() { return gradeTime; }
    public void setGradeTime(java.util.Date gradeTime) { this.gradeTime = gradeTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getStudentAvatar() { return studentAvatar; }
    public void setStudentAvatar(String studentAvatar) { this.studentAvatar = studentAvatar; }
    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }
    public java.math.BigDecimal getScore() { return score; }
    public void setScore(java.math.BigDecimal score) { this.score = score; }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("submissionId", getSubmissionId())
            .append("recordId", getRecordId())
            .append("taskId", getTaskId())
            .append("submissionContent", getSubmissionContent())
            .append("submissionFile", getSubmissionFile())
            .append("submissionTime", getSubmissionTime())
            .append("isGraded", getIsGraded())
            .append("gradeComment", getGradeComment())
            .append("userId", getUserId())
            .append("gradeScore", getGradeScore())
            .append("graderId", getGraderId())
            .append("gradeTime", getGradeTime())
            .append("status", getStatus())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .append("tenantId", getTenantId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
