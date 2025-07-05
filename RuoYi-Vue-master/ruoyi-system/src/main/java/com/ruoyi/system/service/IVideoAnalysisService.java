package com.ruoyi.system.service;

import com.ruoyi.system.domain.VideoAnalysisResult;
import com.ruoyi.system.domain.VideoResource;

import java.util.List;

/**
 * 视频分析服务接口
 * 
 * @author ruoyi
 * @date 2025-01-20
 */
public interface IVideoAnalysisService 
{
    /**
     * 查询视频分析结果
     * 
     * @param analysisId 分析结果ID
     * @return 视频分析结果
     */
    public VideoAnalysisResult selectVideoAnalysisResultById(Long analysisId);

    /**
     * 查询视频分析结果列表
     * 
     * @param videoAnalysisResult 视频分析结果
     * @return 视频分析结果集合
     */
    public List<VideoAnalysisResult> selectVideoAnalysisResultList(VideoAnalysisResult videoAnalysisResult);

    /**
     * 新增视频分析结果
     * 
     * @param videoAnalysisResult 视频分析结果
     * @return 结果
     */
    public int insertVideoAnalysisResult(VideoAnalysisResult videoAnalysisResult);

    /**
     * 修改视频分析结果
     * 
     * @param videoAnalysisResult 视频分析结果
     * @return 结果
     */
    public int updateVideoAnalysisResult(VideoAnalysisResult videoAnalysisResult);

    /**
     * 批量删除视频分析结果
     * 
     * @param analysisIds 需要删除的分析结果ID
     * @return 结果
     */
    public int deleteVideoAnalysisResultByIds(Long[] analysisIds);

    /**
     * 删除视频分析结果信息
     * 
     * @param analysisId 分析结果ID
     * @return 结果
     */
    public int deleteVideoAnalysisResultById(Long analysisId);

    /**
     * 分析视频内容
     * 
     * @param videoId 视频ID
     * @return 分析结果
     */
    public VideoAnalysisResult analyzeVideoContent(Long videoId);

    /**
     * 批量分析待分析视频
     * 
     * @return 分析结果
     */
    public List<VideoAnalysisResult> batchAnalyzePendingVideos();

    /**
     * 获取待分析视频列表
     * 
     * @return 待分析视频列表
     */
    public List<VideoResource> getPendingAnalysisVideos();

    /**
     * 更新视频分析状态
     * 
     * @param videoId 视频ID
     * @param status 分析状态
     * @param errorMessage 错误信息（如果有）
     */
    public void updateVideoAnalysisStatus(Long videoId, Integer status, String errorMessage);

    /**
     * 根据视频ID获取分析结果
     * 
     * @param videoId 视频ID
     * @return 分析结果
     */
    public VideoAnalysisResult getAnalysisResultByVideoId(Long videoId);

    /**
     * 重新分析视频
     * 
     * @param videoId 视频ID
     * @return 分析结果
     */
    public VideoAnalysisResult reanalyzeVideo(Long videoId);

    /**
     * 查询视频分析进度（百分比0-100）
     * @param videoId 视频ID
     * @return 进度百分比
     */
    int getAnalysisProgress(Long videoId);
} 