package com.ruoyi.system.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 课程能力要求，一个课程包含多个能力要求对象 course_skill_requirement
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
public class CourseSkillRequirement extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 要求ID，主键，自增 */
    private Long requirementId;

    /** 课程ID，关联course表 */
    @Excel(name = "课程ID，关联course表")
    private Long courseId;

    /** 能力名称（如"Python编程"） */
    @Excel(name = "能力名称", readConverterExp = "如='Python编程'")
    private String skillName;

    /** 能力层级（1-基础，2-进阶，3-精通） */
    @Excel(name = "能力层级", readConverterExp = "1=-基础，2-进阶，3-精通")
    private Long skillLevel;

    /** 达标分数（0-100，如80分达标） */
    @Excel(name = "达标分数", readConverterExp = "0=-100，如80分达标")
    private BigDecimal requiredScore;

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

    public void setSkillLevel(Long skillLevel) 
    {
        this.skillLevel = skillLevel;
    }

    public Long getSkillLevel() 
    {
        return skillLevel;
    }

    public void setRequiredScore(BigDecimal requiredScore) 
    {
        this.requiredScore = requiredScore;
    }

    public BigDecimal getRequiredScore() 
    {
        return requiredScore;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("requirementId", getRequirementId())
            .append("courseId", getCourseId())
            .append("skillName", getSkillName())
            .append("skillLevel", getSkillLevel())
            .append("requiredScore", getRequiredScore())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
