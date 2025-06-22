package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 学习记录，记录学生的课程学习关联信息对象 learning_record
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
public class LearningRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 记录ID，主键，自增 */
    private Long recordId;

    /** 用户ID，关联sys_user表（学生） */
    @Excel(name = "用户ID，关联sys_user表", readConverterExp = "学=生")
    private Long userId;

    /** 课程ID，关联course表 */
    @Excel(name = "课程ID，关联course表")
    private Long courseId;

    /** 选课时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "选课时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date joinTime;

    /** 课程进度（0-未开始，100-已完成） */
    @Excel(name = "课程进度", readConverterExp = "0=-未开始，100-已完成")
    private Long courseProgress;

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

    public void setJoinTime(Date joinTime) 
    {
        this.joinTime = joinTime;
    }

    public Date getJoinTime() 
    {
        return joinTime;
    }

    public void setCourseProgress(Long courseProgress) 
    {
        this.courseProgress = courseProgress;
    }

    public Long getCourseProgress() 
    {
        return courseProgress;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("recordId", getRecordId())
            .append("userId", getUserId())
            .append("courseId", getCourseId())
            .append("joinTime", getJoinTime())
            .append("courseProgress", getCourseProgress())
            .toString();
    }
}
