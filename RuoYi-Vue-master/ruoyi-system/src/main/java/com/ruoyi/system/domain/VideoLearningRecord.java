package com.ruoyi.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 视频学习记录，记录学生观看视频的行为数据对象 video_learning_record
 * 
 * @author ruoyi
 * @date 2025-06-24
 */
public class VideoLearningRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 记录ID，主键，自增 */
    private Long recordId;

    /** 学习记录ID，关联learning_record表 */
    @Excel(name = "学习记录ID，关联learning_record表")
    private Long learningRecordId;

    /** 资源ID，关联learning_resource表（视频） */
    @Excel(name = "资源ID，关联learning_resource表", readConverterExp = "视=频")
    private Long resourceId;

    /** 视频总时长（秒） */
    @Excel(name = "视频总时长", readConverterExp = "秒=")
    private Long totalDuration;

    /** 观看时长（秒） */
    @Excel(name = "观看时长", readConverterExp = "秒=")
    private Long watchedDuration;

    /** 跳过片段（时间戳，如 0-10,20-30 ） */
    @Excel(name = "跳过片段", readConverterExp = "时=间戳")
    private String skipSegments;

    /** 重复观看片段（时间戳） */
    @Excel(name = "重复观看片段", readConverterExp = "时=间戳")
    private String repeatSegments;

    /** 完成率（%） */
    @Excel(name = "完成率", readConverterExp = "%=")
    private BigDecimal completionRate;

    /** 最后观看时间 */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX", timezone = "GMT+8")
    @Excel(name = "最后观看时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date lastWatchTime;

    /** 查询辅助字段：用户ID（通过 learning_record 关联） */
    private Long userId;

    /** 学生姓名（关联sys_user） */
    private String studentName;

    /** 学生头像 */
    private String studentAvatar;

    /** 资源名称（视频名） */
    private String resourceName;

    /** 资源分类/类型 */
    private String resourceCategory;

    /** 课程名称 */
    private String courseName;

    /** 完成率范围（开始） */
    private Integer completionRateStart;

    /** 完成率范围（结束） */
    private Integer completionRateEnd;

    /** 完成状态 */
    private String completionStatus;

    public void setRecordId(Long recordId) 
    {
        this.recordId = recordId;
    }

    public Long getRecordId() 
    {
        return recordId;
    }

    public void setLearningRecordId(Long learningRecordId) 
    {
        this.learningRecordId = learningRecordId;
    }

    public Long getLearningRecordId() 
    {
        return learningRecordId;
    }

    public void setResourceId(Long resourceId) 
    {
        this.resourceId = resourceId;
    }

    public Long getResourceId() 
    {
        return resourceId;
    }

    public void setTotalDuration(Long totalDuration) 
    {
        this.totalDuration = totalDuration;
    }

    public Long getTotalDuration() 
    {
        return totalDuration;
    }

    public void setWatchedDuration(Long watchedDuration) 
    {
        this.watchedDuration = watchedDuration;
    }

    public Long getWatchedDuration() 
    {
        return watchedDuration;
    }

    public void setSkipSegments(String skipSegments) 
    {
        this.skipSegments = skipSegments;
    }

    public String getSkipSegments() 
    {
        return skipSegments;
    }

    public void setRepeatSegments(String repeatSegments) 
    {
        this.repeatSegments = repeatSegments;
    }

    public String getRepeatSegments() 
    {
        return repeatSegments;
    }

    public void setCompletionRate(BigDecimal completionRate) 
    {
        this.completionRate = completionRate;
    }

    public BigDecimal getCompletionRate() 
    {
        return completionRate;
    }

    public void setLastWatchTime(Date lastWatchTime) 
    {
        this.lastWatchTime = lastWatchTime;
    }

    public Date getLastWatchTime() 
    {
        return lastWatchTime;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getStudentAvatar() { return studentAvatar; }
    public void setStudentAvatar(String studentAvatar) { this.studentAvatar = studentAvatar; }
    public String getResourceName() { return resourceName; }
    public void setResourceName(String resourceName) { this.resourceName = resourceName; }
    public String getResourceCategory() { return resourceCategory; }
    public void setResourceCategory(String resourceCategory) { this.resourceCategory = resourceCategory; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public Integer getCompletionRateStart() { return completionRateStart; }
    public void setCompletionRateStart(Integer completionRateStart) { this.completionRateStart = completionRateStart; }
    public Integer getCompletionRateEnd() { return completionRateEnd; }
    public void setCompletionRateEnd(Integer completionRateEnd) { this.completionRateEnd = completionRateEnd; }
    
    public String getCompletionStatus() { return completionStatus; }
    public void setCompletionStatus(String completionStatus) { this.completionStatus = completionStatus; }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("recordId", getRecordId())
            .append("learningRecordId", getLearningRecordId())
            .append("resourceId", getResourceId())
            .append("totalDuration", getTotalDuration())
            .append("watchedDuration", getWatchedDuration())
            .append("skipSegments", getSkipSegments())
            .append("repeatSegments", getRepeatSegments())
            .append("completionRate", getCompletionRate())
            .append("lastWatchTime", getLastWatchTime())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("resourceCategory", getResourceCategory())
            .append("courseName", getCourseName())
            .toString();
    }
}
