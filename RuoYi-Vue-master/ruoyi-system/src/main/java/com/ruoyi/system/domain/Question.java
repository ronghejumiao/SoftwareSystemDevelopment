package com.ruoyi.system.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 题目，存储题库中的题目信息对象 question
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
public class Question extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 题目ID，主键，自增 */
    private Long questionId;

    /** 题库ID，关联question_bank表 */
    @Excel(name = "题库ID，关联question_bank表")
    private Long bankId;

    /** 知识点ID，关联knowledge_node表 */
    @Excel(name = "知识点ID，关联knowledge_node表")
    private Long knowledgeNodeId;

    /** 题目类型（选择、填空、简答等） */
    @Excel(name = "题目类型", readConverterExp = "选=择、填空、简答等")
    private String questionType;

    /** 题目内容 */
    @Excel(name = "题目内容")
    private String questionContent;

    /** 选项内容（选择题专用） */
    @Excel(name = "选项内容", readConverterExp = "选=择题专用")
    private String options;

    /** 答案 */
    @Excel(name = "答案")
    private String answer;

    /** 难度等级（简单、中等、困难） */
    @Excel(name = "难度等级", readConverterExp = "简=单、中等、困难")
    private String difficultyLevel;

    /** 分值 */
    @Excel(name = "分值")
    private BigDecimal score;

    public void setQuestionId(Long questionId) 
    {
        this.questionId = questionId;
    }

    public Long getQuestionId() 
    {
        return questionId;
    }

    public void setBankId(Long bankId) 
    {
        this.bankId = bankId;
    }

    public Long getBankId() 
    {
        return bankId;
    }

    public void setKnowledgeNodeId(Long knowledgeNodeId) 
    {
        this.knowledgeNodeId = knowledgeNodeId;
    }

    public Long getKnowledgeNodeId() 
    {
        return knowledgeNodeId;
    }

    public void setQuestionType(String questionType) 
    {
        this.questionType = questionType;
    }

    public String getQuestionType() 
    {
        return questionType;
    }

    public void setQuestionContent(String questionContent) 
    {
        this.questionContent = questionContent;
    }

    public String getQuestionContent() 
    {
        return questionContent;
    }

    public void setOptions(String options) 
    {
        this.options = options;
    }

    public String getOptions() 
    {
        return options;
    }

    public void setAnswer(String answer) 
    {
        this.answer = answer;
    }

    public String getAnswer() 
    {
        return answer;
    }

    public void setDifficultyLevel(String difficultyLevel) 
    {
        this.difficultyLevel = difficultyLevel;
    }

    public String getDifficultyLevel() 
    {
        return difficultyLevel;
    }

    public void setScore(BigDecimal score) 
    {
        this.score = score;
    }

    public BigDecimal getScore() 
    {
        return score;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("questionId", getQuestionId())
            .append("bankId", getBankId())
            .append("knowledgeNodeId", getKnowledgeNodeId())
            .append("questionType", getQuestionType())
            .append("questionContent", getQuestionContent())
            .append("options", getOptions())
            .append("answer", getAnswer())
            .append("difficultyLevel", getDifficultyLevel())
            .append("score", getScore())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
