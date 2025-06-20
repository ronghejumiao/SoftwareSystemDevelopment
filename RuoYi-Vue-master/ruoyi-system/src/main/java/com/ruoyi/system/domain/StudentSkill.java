package com.ruoyi.system.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 学生能力，基于课程能力要求构建对象 student_skill
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
public class StudentSkill extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID，主键，自增 */
    private Long id;

    /** 学生ID，关联sys_user表 */
    @Excel(name = "学生ID，关联sys_user表")
    private Long studentId;

    /** 能力要求ID，关联course_skill_requirement表 */
    @Excel(name = "能力要求ID，关联course_skill_requirement表")
    private Long requirementId;

    /** 能力评分（0-100） */
    @Excel(name = "能力评分", readConverterExp = "0=-100")
    private BigDecimal skillScore;

    /** 评分更新原因 */
    @Excel(name = "评分更新原因")
    private String updateReason;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setStudentId(Long studentId) 
    {
        this.studentId = studentId;
    }

    public Long getStudentId() 
    {
        return studentId;
    }

    public void setRequirementId(Long requirementId) 
    {
        this.requirementId = requirementId;
    }

    public Long getRequirementId() 
    {
        return requirementId;
    }

    public void setSkillScore(BigDecimal skillScore) 
    {
        this.skillScore = skillScore;
    }

    public BigDecimal getSkillScore() 
    {
        return skillScore;
    }

    public void setUpdateReason(String updateReason) 
    {
        this.updateReason = updateReason;
    }

    public String getUpdateReason() 
    {
        return updateReason;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("studentId", getStudentId())
            .append("requirementId", getRequirementId())
            .append("skillScore", getSkillScore())
            .append("updateTime", getUpdateTime())
            .append("updateReason", getUpdateReason())
            .toString();
    }
}
