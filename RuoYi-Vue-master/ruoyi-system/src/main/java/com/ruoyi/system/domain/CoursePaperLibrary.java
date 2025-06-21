package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 课程试卷库，一个课程对应一个试卷库对象 course_paper_library
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
public class CoursePaperLibrary extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 试卷库ID，主键，自增 */
    private Long libraryId;

    /** 课程ID，关联course表 */
    @Excel(name = "课程ID，关联course表")
    private Long courseId;

    /** 试卷库名称（如"人工智能课程试卷库"） */
    @Excel(name = "试卷库名称", readConverterExp = "如='人工智能课程试卷库'")
    private String libraryName;

    /** 试卷库描述 */
    @Excel(name = "试卷库描述")
    private String libraryDesc;

    public void setLibraryId(Long libraryId) 
    {
        this.libraryId = libraryId;
    }

    public Long getLibraryId() 
    {
        return libraryId;
    }

    public void setCourseId(Long courseId) 
    {
        this.courseId = courseId;
    }

    public Long getCourseId() 
    {
        return courseId;
    }

    public void setLibraryName(String libraryName) 
    {
        this.libraryName = libraryName;
    }

    public String getLibraryName() 
    {
        return libraryName;
    }

    public void setLibraryDesc(String libraryDesc) 
    {
        this.libraryDesc = libraryDesc;
    }

    public String getLibraryDesc() 
    {
        return libraryDesc;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("libraryId", getLibraryId())
            .append("courseId", getCourseId())
            .append("libraryName", getLibraryName())
            .append("libraryDesc", getLibraryDesc())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
