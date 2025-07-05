package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.VideoResourceMapper;
import com.ruoyi.system.domain.VideoResource;
import com.ruoyi.system.service.IVideoResourceService;

/**
 * 视频学习资源Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-06-23
 */
@Service
public class VideoResourceServiceImpl implements IVideoResourceService 
{
    @Autowired
    private VideoResourceMapper videoResourceMapper;

    /**
     * 查询视频学习资源
     * 
     * @param videoId 视频学习资源主键
     * @return 视频学习资源
     */
    @Override
    public VideoResource selectVideoResourceByVideoId(Long videoId)
    {
        return videoResourceMapper.selectVideoResourceByVideoId(videoId);
    }

    /**
     * 查询视频学习资源列表
     * 
     * @param videoResource 视频学习资源
     * @return 视频学习资源
     */
    @Override
    public List<VideoResource> selectVideoResourceList(VideoResource videoResource)
    {
        return videoResourceMapper.selectVideoResourceList(videoResource);
    }

    /**
     * 新增视频学习资源
     * 
     * @param videoResource 视频学习资源
     * @return 结果
     */
    @Override
    public int insertVideoResource(VideoResource videoResource)
    {
        return videoResourceMapper.insertVideoResource(videoResource);
    }

    /**
     * 修改视频学习资源
     * 
     * @param videoResource 视频学习资源
     * @return 结果
     */
    @Override
    public int updateVideoResource(VideoResource videoResource)
    {
        return videoResourceMapper.updateVideoResource(videoResource);
    }

    /**
     * 批量删除视频学习资源
     * 
     * @param videoIds 需要删除的视频学习资源主键
     * @return 结果
     */
    @Override
    public int deleteVideoResourceByVideoIds(Long[] videoIds)
    {
        return videoResourceMapper.deleteVideoResourceByVideoIds(videoIds);
    }

    /**
     * 删除视频学习资源信息
     * 
     * @param videoId 视频学习资源主键
     * @return 结果
     */
    @Override
    public int deleteVideoResourceByVideoId(Long videoId)
    {
        return videoResourceMapper.deleteVideoResourceByVideoId(videoId);
    }

    @Override
    public java.util.List<com.ruoyi.system.domain.VideoResource> selectPendingAnalysisVideos() {
        com.ruoyi.system.domain.VideoResource query = new com.ruoyi.system.domain.VideoResource();
        query.setAnalysisStatus(0); // 0-未分析
        return videoResourceMapper.selectVideoResourceList(query);
    }

    @Override
    public com.ruoyi.system.domain.VideoResource selectVideoResourceById(Long videoId) {
        return videoResourceMapper.selectVideoResourceByVideoId(videoId);
    }

    @Override
    public java.util.List<com.ruoyi.system.domain.VideoResource> selectVideoResourceListByCourseId(Long courseId) {
        com.ruoyi.system.domain.VideoResource query = new com.ruoyi.system.domain.VideoResource();
        query.setCourseId(courseId);
        return videoResourceMapper.selectVideoResourceList(query);
    }
}
