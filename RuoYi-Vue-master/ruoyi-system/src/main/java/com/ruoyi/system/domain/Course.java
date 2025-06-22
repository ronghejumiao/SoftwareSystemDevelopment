package com.ruoyi.system.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 课程信息，存储课程的基本信息对象 course
 * 
 * @author ruoyi
 * @date 2025-06-22
 */
public class Course extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 课程ID */
    private Long courseId;

    /** 课程名称 */
    @Excel(name = "课程名称")
    private String courseName;

    /** 课程编号 */
    @Excel(name = "课程编号")
    private String courseCode;

    /** 课程分类 */
    @Excel(name = "课程分类")
    private String courseCategory;

    /** 授课教师ID */
    @Excel(name = "授课教师ID")
    private Long teacherId;

    /** 课程学分 */
    @Excel(name = "课程学分")
    private BigDecimal credit;

    /** 课程学时 */
    @Excel(name = "课程学时")
    private Long hours;

    /** 课程描述 */
    @Excel(name = "课程描述")
    private String courseDesc;

    /** 课程状态（1-启用，0-停用） */
    @Excel(name = "课程状态", readConverterExp = "1=-启用，0-停用")
    private String status;

    /** 课程的图片 */
    @Excel(name = "课程的图片")
    private String courseImg;

    public void setCourseId(Long courseId) 
    {
        this.courseId = courseId;
    }

    public Long getCourseId() 
    {
        return courseId;
    }

    public void setCourseName(String courseName) 
    {
        this.courseName = courseName;
    }

    public String getCourseName() 
    {
        return courseName;
    }

    public void setCourseCode(String courseCode) 
    {
        this.courseCode = courseCode;
    }

    public String getCourseCode() 
    {
        return courseCode;
    }

    public void setCourseCategory(String courseCategory) 
    {
        this.courseCategory = courseCategory;
    }

    public String getCourseCategory() 
    {
        return courseCategory;
    }

    public void setTeacherId(Long teacherId) 
    {
        this.teacherId = teacherId;
    }

    public Long getTeacherId() 
    {
        return teacherId;
    }

    public void setCredit(BigDecimal credit) 
    {
        this.credit = credit;
    }

    public BigDecimal getCredit() 
    {
        return credit;
    }

    public void setHours(Long hours) 
    {
        this.hours = hours;
    }

    public Long getHours() 
    {
        return hours;
    }

    public void setCourseDesc(String courseDesc) 
    {
        this.courseDesc = courseDesc;
    }

    public String getCourseDesc() 
    {
        return courseDesc;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setCourseImg(String courseImg) 
    {
        this.courseImg = courseImg;
    }

    public String getCourseImg() 
    {
        return courseImg;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("courseId", getCourseId())
            .append("courseName", getCourseName())
            .append("courseCode", getCourseCode())
            .append("courseCategory", getCourseCategory())
            .append("teacherId", getTeacherId())
            .append("credit", getCredit())
            .append("hours", getHours())
            .append("courseDesc", getCourseDesc())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("status", getStatus())
            .append("courseImg", getCourseImg())
            .toString();
    }
}
