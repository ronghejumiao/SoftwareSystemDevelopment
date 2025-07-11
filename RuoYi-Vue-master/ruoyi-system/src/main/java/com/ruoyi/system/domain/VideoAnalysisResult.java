package com.ruoyi.system.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 视频分析结果对象 video_analysis_result
 * 
 * @author ruoyi
 * @date 2025-01-20
 */
public class VideoAnalysisResult extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 分析结果ID */
    private Long analysisId;

    /** 视频ID */
    @Excel(name = "视频ID")
    private Long videoId;

    /** 分析状态：0-未分析，1-分析中，2-分析完成，3-分析失败 */
    @Excel(name = "分析状态", readConverterExp = "0=未分析,1=分析中,2=分析完成,3=分析失败")
    private Integer analysisStatus;

    /** 视频总时长（秒） */
    @Excel(name = "视频总时长")
    private Integer totalDuration;

    /** 分段数量 */
    @Excel(name = "分段数量")
    private Integer segmentsCount;

    /** 使用的模型 */
    @Excel(name = "使用的模型")
    private String modelUsed;

    /** 分析完成时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "分析完成时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date analysisTime;

    /** 错误信息 */
    @Excel(name = "错误信息")
    private String errorMessage;

    /** 视频分段列表 */
    private List<VideoSegment> segments;

    /** 分析元数据 */
    private AnalysisMetadata analysisMetadata;

    /** 分析内容（JSON） */
    private String videoAnalysis;

    public void setAnalysisId(Long analysisId) 
    {
        this.analysisId = analysisId;
    }

    public Long getAnalysisId() 
    {
        return analysisId;
    }

    public void setVideoId(Long videoId) 
    {
        this.videoId = videoId;
    }

    public Long getVideoId() 
    {
        return videoId;
    }

    public void setAnalysisStatus(Integer analysisStatus) 
    {
        this.analysisStatus = analysisStatus;
    }

    public Integer getAnalysisStatus() 
    {
        return analysisStatus;
    }

    public void setTotalDuration(Integer totalDuration) 
    {
        this.totalDuration = totalDuration;
    }

    public Integer getTotalDuration() 
    {
        return totalDuration;
    }

    public void setSegmentsCount(Integer segmentsCount) 
    {
        this.segmentsCount = segmentsCount;
    }

    public Integer getSegmentsCount() 
    {
        return segmentsCount;
    }

    public void setModelUsed(String modelUsed) 
    {
        this.modelUsed = modelUsed;
    }

    public String getModelUsed() 
    {
        return modelUsed;
    }

    public void setAnalysisTime(Date analysisTime) 
    {
        this.analysisTime = analysisTime;
    }

    public Date getAnalysisTime() 
    {
        return analysisTime;
    }

    public void setErrorMessage(String errorMessage) 
    {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() 
    {
        return errorMessage;
    }

    public List<VideoSegment> getSegments() 
    {
        return segments;
    }

    public void setSegments(List<VideoSegment> segments) 
    {
        this.segments = segments;
    }

    public AnalysisMetadata getAnalysisMetadata() 
    {
        return analysisMetadata;
    }

    public void setAnalysisMetadata(AnalysisMetadata analysisMetadata) 
    {
        this.analysisMetadata = analysisMetadata;
    }

    public String getVideoAnalysis() {
        return videoAnalysis;
    }

    public void setVideoAnalysis(String videoAnalysis) {
        this.videoAnalysis = videoAnalysis;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("analysisId", getAnalysisId())
            .append("videoId", getVideoId())
            .append("analysisStatus", getAnalysisStatus())
            .append("totalDuration", getTotalDuration())
            .append("segmentsCount", getSegmentsCount())
            .append("modelUsed", getModelUsed())
            .append("analysisTime", getAnalysisTime())
            .append("errorMessage", getErrorMessage())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }

    /**
     * 视频分段信息
     */
    public static class VideoSegment {
        private Integer segmentId;
        private Integer startTime;
        private Integer endTime;
        private Integer duration;
        private String contentSummary;

        // Getters and Setters
        public Integer getSegmentId() { return segmentId; }
        public void setSegmentId(Integer segmentId) { this.segmentId = segmentId; }
        
        public Integer getStartTime() { return startTime; }
        public void setStartTime(Integer startTime) { this.startTime = startTime; }
        
        public Integer getEndTime() { return endTime; }
        public void setEndTime(Integer endTime) { this.endTime = endTime; }
        
        public Integer getDuration() { return duration; }
        public void setDuration(Integer duration) { this.duration = duration; }
        
        public String getContentSummary() { return contentSummary; }
        public void setContentSummary(String contentSummary) { this.contentSummary = contentSummary; }
    }

    /**
     * 分析元数据
     */
    public static class AnalysisMetadata {
        private String modelUsed;
        private Date analysisTime;
        private Integer segmentsCount;
        private Integer totalKnowledgePoints;

        // Getters and Setters
        public String getModelUsed() { return modelUsed; }
        public void setModelUsed(String modelUsed) { this.modelUsed = modelUsed; }
        
        public Date getAnalysisTime() { return analysisTime; }
        public void setAnalysisTime(Date analysisTime) { this.analysisTime = analysisTime; }
        
        public Integer getSegmentsCount() { return segmentsCount; }
        public void setSegmentsCount(Integer segmentsCount) { this.segmentsCount = segmentsCount; }
        
        public Integer getTotalKnowledgePoints() { return totalKnowledgePoints; }
        public void setTotalKnowledgePoints(Integer totalKnowledgePoints) { this.totalKnowledgePoints = totalKnowledgePoints; }
    }
} 