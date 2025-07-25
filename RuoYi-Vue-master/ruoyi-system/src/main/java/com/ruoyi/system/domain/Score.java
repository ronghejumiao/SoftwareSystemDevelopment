package com.ruoyi.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 成绩管理对象 score
 * 
 * @author ruoyi
 * @date 2025-06-22
 */
public class Score extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 成绩ID，主键，自增 */
    @Excel(name = "成绩ID，主键，自增")
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

    /** 得分(0-100) */
    @Excel(name = "得分(0-100)")
    private BigDecimal score;

    /** 成绩描述，支持模糊查询 */
    @Excel(name = "成绩描述，支持模糊查询")
    private String scoreDesc;

    /** 成绩状态(0-无效 1-有效) */
    @Excel(name = "成绩状态(0-无效 1-有效)")
    private Long scoreStatus;

    /** 提交时间，默认当前时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "提交时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date submitTime;

    /** 试卷详情，存储学生作答情况 */
    @Excel(name = "试卷详情")
    private String answerDetails;

    /** 课程ID */
    private Long courseId;

    /** 课程名称 */
    private String courseName;

    /** 学习记录ID */
    private Long recordId;

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

    public void setScoreStatus(Long scoreStatus) 
    {
        this.scoreStatus = scoreStatus;
    }

    public Long getScoreStatus() 
    {
        return scoreStatus;
    }

    public void setSubmitTime(Date submitTime) 
    {
        this.submitTime = submitTime;
    }

    public Date getSubmitTime() 
    {
        return submitTime;
    }

    public void setAnswerDetails(String answerDetails) 
    {
        this.answerDetails = answerDetails;
    }

    public String getAnswerDetails() 
    {
        return answerDetails;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public Long getRecordId() {
        return recordId;
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
            .append("scoreStatus", getScoreStatus())
            .append("submitTime", getSubmitTime())
            .append("courseId", getCourseId())
            .append("courseName", getCourseName())
            .append("answerDetails", getAnswerDetails())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
