package com.ruoyi.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 成绩，记录学生的学习成绩信息对象 score
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
public class Score extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 成绩ID，主键，自增 */
    private Long scoreId;

    /** 学习记录ID，关联learning_record表 */
    @Excel(name = "学习记录ID，关联learning_record表")
    private Long learningRecordId;

    /** 任务ID，关联learning_task表 */
    @Excel(name = "任务ID，关联learning_task表")
    private Long taskId;

    /** 试卷ID，关联test_paper表 */
    @Excel(name = "试卷ID，关联test_paper表")
    private Long paperId;

    /** 得分 */
    @Excel(name = "得分")
    private BigDecimal score;

    /** 成绩描述 */
    @Excel(name = "成绩描述")
    private String scoreDesc;

    /** 提交时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "提交时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date submitTime;

    public void setScoreId(Long scoreId) 
    {
        this.scoreId = scoreId;
    }

    public Long getScoreId() 
    {
        return scoreId;
    }

    public void setLearningRecordId(Long learningRecordId) 
    {
        this.learningRecordId = learningRecordId;
    }

    public Long getLearningRecordId() 
    {
        return learningRecordId;
    }

    public void setTaskId(Long taskId) 
    {
        this.taskId = taskId;
    }

    public Long getTaskId() 
    {
        return taskId;
    }

    public void setPaperId(Long paperId) 
    {
        this.paperId = paperId;
    }

    public Long getPaperId() 
    {
        return paperId;
    }

    public void setScore(BigDecimal score) 
    {
        this.score = score;
    }

    public BigDecimal getScore() 
    {
        return score;
    }

    public void setScoreDesc(String scoreDesc) 
    {
        this.scoreDesc = scoreDesc;
    }

    public String getScoreDesc() 
    {
        return scoreDesc;
    }

    public void setSubmitTime(Date submitTime) 
    {
        this.submitTime = submitTime;
    }

    public Date getSubmitTime() 
    {
        return submitTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("scoreId", getScoreId())
            .append("learningRecordId", getLearningRecordId())
            .append("taskId", getTaskId())
            .append("paperId", getPaperId())
            .append("score", getScore())
            .append("scoreDesc", getScoreDesc())
            .append("submitTime", getSubmitTime())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
