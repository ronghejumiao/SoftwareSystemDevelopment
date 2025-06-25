package com.ruoyi.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 学习记录，记录学生的课程学习进度和关联信息对象 learning_record
 * 
 * @author ruoyi
 * @date 2025-06-22
 */
public class LearningRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 记录ID */
    private Long recordId;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 课程ID */
    @Excel(name = "课程ID")
    private Long courseId;

    /** 记录类型 */
    @Excel(name = "记录类型")
    private Long recordType;

    /** 选课/开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "选课/开始时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date joinTime;

    /** 结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "结束时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date endTime;

    /** 课程进度 */
    @Excel(name = "课程进度")
    private BigDecimal courseProgress;

    /** 视频学习进度 */
    @Excel(name = "视频学习进度")
    private BigDecimal videoProgress;

    /** 任务完成进度 */
    @Excel(name = "任务完成进度")
    private BigDecimal taskProgress;

    /** 考试完成进度 */
    @Excel(name = "考试完成进度")
    private BigDecimal examProgress;

    /** 总成绩 */
    @Excel(name = "总成绩")
    private BigDecimal totalScore;

    /** 课程状态 */
    @Excel(name = "课程状态")
    private Long courseStatus;

    public void setRecordId(Long recordId) 
    {
        this.recordId = recordId;
    }

    public Long getRecordId() 
    {
        return recordId;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setCourseId(Long courseId) 
    {
        this.courseId = courseId;
    }

    public Long getCourseId() 
    {
        return courseId;
    }

    public void setRecordType(Long recordType) 
    {
        this.recordType = recordType;
    }

    public Long getRecordType() 
    {
        return recordType;
    }

    public void setJoinTime(Date joinTime) 
    {
        this.joinTime = joinTime;
    }

    public Date getJoinTime() 
    {
        return joinTime;
    }

    public void setEndTime(Date endTime) 
    {
        this.endTime = endTime;
    }

    public Date getEndTime() 
    {
        return endTime;
    }

    public void setCourseProgress(BigDecimal courseProgress) 
    {
        this.courseProgress = courseProgress;
    }

    public BigDecimal getCourseProgress() 
    {
        return courseProgress;
    }

    public void setVideoProgress(BigDecimal videoProgress) 
    {
        this.videoProgress = videoProgress;
    }

    public BigDecimal getVideoProgress() 
    {
        return videoProgress;
    }

    public void setTaskProgress(BigDecimal taskProgress) 
    {
        this.taskProgress = taskProgress;
    }

    public BigDecimal getTaskProgress() 
    {
        return taskProgress;
    }

    public void setExamProgress(BigDecimal examProgress) 
    {
        this.examProgress = examProgress;
    }

    public BigDecimal getExamProgress() 
    {
        return examProgress;
    }

    public void setTotalScore(BigDecimal totalScore) 
    {
        this.totalScore = totalScore;
    }

    public BigDecimal getTotalScore() 
    {
        return totalScore;
    }

    public void setCourseStatus(Long courseStatus) 
    {
        this.courseStatus = courseStatus;
    }

    public Long getCourseStatus() 
    {
        return courseStatus;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("recordId", getRecordId())
            .append("userId", getUserId())
            .append("courseId", getCourseId())
            .append("recordType", getRecordType())
            .append("joinTime", getJoinTime())
            .append("endTime", getEndTime())
            .append("courseProgress", getCourseProgress())
            .append("videoProgress", getVideoProgress())
            .append("taskProgress", getTaskProgress())
            .append("examProgress", getExamProgress())
            .append("totalScore", getTotalScore())
            .append("courseStatus", getCourseStatus())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
