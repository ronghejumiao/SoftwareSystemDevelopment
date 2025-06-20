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
 * @date 2025-06-20
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

    /** 跳过片段（时间戳，如“0-10,20-30”） */
    @Excel(name = "跳过片段", readConverterExp = "时=间戳，如“0-10,20-30”")
    private String skipSegments;

    /** 重复观看片段（时间戳） */
    @Excel(name = "重复观看片段", readConverterExp = "时=间戳")
    private String repeatSegments;

    /** 完成率（%） */
    @Excel(name = "完成率", readConverterExp = "%=")
    private BigDecimal completionRate;

    /** 最后观看时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "最后观看时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date lastWatchTime;

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
            .toString();
    }
}
