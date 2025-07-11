package com.ruoyi.system.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

public class LearningResourceAnalysis {
    private Long analysisId;
    private Long resourceId;
    private Integer status; // 0-未分析，1-分析中，2-分析完成，3-分析失败
    private String contentSummary;
    private String modelUsed;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date analysisTime;
    private String errorMessage;
    private Date createTime;
    private Date updateTime;

    public Long getAnalysisId() { return analysisId; }
    public void setAnalysisId(Long analysisId) { this.analysisId = analysisId; }
    public Long getResourceId() { return resourceId; }
    public void setResourceId(Long resourceId) { this.resourceId = resourceId; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getContentSummary() { return contentSummary; }
    public void setContentSummary(String contentSummary) { this.contentSummary = contentSummary; }
    public String getModelUsed() { return modelUsed; }
    public void setModelUsed(String modelUsed) { this.modelUsed = modelUsed; }
    public Date getAnalysisTime() { return analysisTime; }
    public void setAnalysisTime(Date analysisTime) { this.analysisTime = analysisTime; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
} 