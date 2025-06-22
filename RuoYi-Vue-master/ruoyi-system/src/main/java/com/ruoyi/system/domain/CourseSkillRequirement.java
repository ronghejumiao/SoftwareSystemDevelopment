package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 课程能力要求对象 course_skill_requirement
 * 
 * @author ruoyi
 * @date 2025-06-22
 */
public class CourseSkillRequirement extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 要求ID */
    private Long requirementId;

    /** 课程ID */
    @Excel(name = "课程ID")
    private Long courseId;

    /** 能力名称 */
    @Excel(name = "能力名称")
    private String skillName;

    /** 课程描述 */
    @Excel(name = "课程描述")
    private String description;

    /** 课堂要求描述 */
    @Excel(name = "课堂要求描述")
    private String requiredText;

    public void setRequirementId(Long requirementId) 
    {
        this.requirementId = requirementId;
    }

    public Long getRequirementId() 
    {
        return requirementId;
    }

    public void setCourseId(Long courseId) 
    {
        this.courseId = courseId;
    }

    public Long getCourseId() 
    {
        return courseId;
    }

    public void setSkillName(String skillName) 
    {
        this.skillName = skillName;
    }

    public String getSkillName() 
    {
        return skillName;
    }

    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }

    public void setRequiredText(String requiredText) 
    {
        this.requiredText = requiredText;
    }

    public String getRequiredText() 
    {
        return requiredText;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("requirementId", getRequirementId())
            .append("courseId", getCourseId())
            .append("skillName", getSkillName())
            .append("description", getDescription())
            .append("requiredText", getRequiredText())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
