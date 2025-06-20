package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 题库，存储课程的题库信息对象 question_bank
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
public class QuestionBank extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 题库ID，主键，自增 */
    private Long bankId;

    /** 课程ID，关联course表 */
    @Excel(name = "课程ID，关联course表")
    private Long courseId;

    /** 题库名称 */
    @Excel(name = "题库名称")
    private String bankName;

    /** 题库描述 */
    @Excel(name = "题库描述")
    private String bankDesc;

    public void setBankId(Long bankId) 
    {
        this.bankId = bankId;
    }

    public Long getBankId() 
    {
        return bankId;
    }

    public void setCourseId(Long courseId) 
    {
        this.courseId = courseId;
    }

    public Long getCourseId() 
    {
        return courseId;
    }

    public void setBankName(String bankName) 
    {
        this.bankName = bankName;
    }

    public String getBankName() 
    {
        return bankName;
    }

    public void setBankDesc(String bankDesc) 
    {
        this.bankDesc = bankDesc;
    }

    public String getBankDesc() 
    {
        return bankDesc;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("bankId", getBankId())
            .append("courseId", getCourseId())
            .append("bankName", getBankName())
            .append("bankDesc", getBankDesc())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
