package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

/**
 * 课程作业对象 course_homework
 * 
 * @author ruoyi
 * @date 2025-06-25
 */
public class CourseHomework extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 作业ID */
    private Long homeworkId;

    /** 课程ID */
    @Excel(name = "课程ID")
    private Long courseId;

    /** 作业名称 */
    @Excel(name = "作业名称")
    private String homeworkName;

    /** 作业描述 */
    @Excel(name = "作业描述")
    private String homeworkDesc;

    /** 截止时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "截止时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date dueDate;

    /** 作业附件路径，json数组字符串 */
    @Excel(name = "作业附件路径，json数组字符串")
    private String filePaths;

    /** 状态（1-启用，0-停用） */
    @Excel(name = "状态", readConverterExp = "1=-启用，0-停用")
    private String status;

    public void setHomeworkId(Long homeworkId) 
    {
        this.homeworkId = homeworkId;
    }

    public Long getHomeworkId() 
    {
        return homeworkId;
    }

    public void setCourseId(Long courseId) 
    {
        this.courseId = courseId;
    }

    public Long getCourseId() 
    {
        return courseId;
    }

    public void setHomeworkName(String homeworkName) 
    {
        this.homeworkName = homeworkName;
    }

    public String getHomeworkName() 
    {
        return homeworkName;
    }

    public void setHomeworkDesc(String homeworkDesc) 
    {
        this.homeworkDesc = homeworkDesc;
    }

    public String getHomeworkDesc() 
    {
        return homeworkDesc;
    }

    public void setDueDate(Date dueDate) 
    {
        this.dueDate = dueDate;
    }

    public Date getDueDate() 
    {
        return dueDate;
    }

    public void setFilePaths(String filePaths) 
    {
        this.filePaths = filePaths;
    }

    public String getFilePaths() 
    {
        return filePaths;
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
            .append("homeworkId", getHomeworkId())
            .append("courseId", getCourseId())
            .append("homeworkName", getHomeworkName())
            .append("homeworkDesc", getHomeworkDesc())
            .append("dueDate", getDueDate())
            .append("filePaths", getFilePaths())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("status", getStatus())
            .append("remark", getRemark())
            .toString();
    }
}
