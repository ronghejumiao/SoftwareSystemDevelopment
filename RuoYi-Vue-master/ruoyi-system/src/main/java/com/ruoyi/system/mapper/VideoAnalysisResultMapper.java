package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.VideoAnalysisResult;
import java.util.List;

/**
 * 视频分析结果Mapper接口
 * 
 * @author ruoyi
 * @date 2025-01-20
 */
public interface VideoAnalysisResultMapper 
{
    /**
     * 查询视频分析结果
     * 
     * @param analysisId 分析结果ID
     * @return 视频分析结果
     */
    public VideoAnalysisResult selectVideoAnalysisResultById(Long analysisId);

    /**
     * 根据视频ID查询分析结果
     * 
     * @param videoId 视频ID
     * @return 视频分析结果
     */
    public VideoAnalysisResult selectVideoAnalysisResultByVideoId(Long videoId);

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
     * 删除视频分析结果
     * 
     * @param analysisId 分析结果ID
     * @return 结果
     */
    public int deleteVideoAnalysisResultById(Long analysisId);

    /**
     * 批量删除视频分析结果
     * 
     * @param analysisIds 需要删除的分析结果ID
     * @return 结果
     */
    public int deleteVideoAnalysisResultByIds(Long[] analysisIds);
} 